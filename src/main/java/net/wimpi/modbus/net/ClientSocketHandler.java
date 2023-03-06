/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

package net.wimpi.modbus.net;

import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.io.SocketStreamTransport;
import net.wimpi.modbus.io.StreamTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract base class for processing client socket.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public abstract class ClientSocketHandler implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(ClientSocketHandler.class);
    protected final List<ModbusTransport> clientTransports = new ArrayList<>();
    private final String host;
    private final int port;
    private boolean working;

    /**
     * Constructs a new <tt>ClientSocketHandler</tt> instance
     *
     * @param host the host to start handling connections.
     * @param port the port to start handling connections.
     */
    public ClientSocketHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Creates some <tt>ModbusTransport</tt>.
     *
     * @return a <tt>ModbusTransport</tt> implementation.
     */
    protected abstract ModbusTransport createTransport(StreamTransport streamTransport);

    /**
     * Returns open state of transport.
     *
     * @return true if the transport is open, false otherwise.
     */
    protected abstract boolean isTransportOpen(ModbusTransport transport);

    @Override
    public void run() {
        working = true;
        ServerSocketChannel serverSocket = null;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(host, port));
            serverSocket.configureBlocking(false);
            log.debug("Started server socket: " + serverSocket);
            while (working) {
                try {
                    SocketChannel clientSocketChannel = serverSocket.accept();
                    if (clientSocketChannel != null) {
                        StreamTransport streamTransport = new SocketStreamTransport(clientSocketChannel.socket());
                        ModbusTransport transport = createTransport(streamTransport);
                        log.debug(String.format("New client connection [%s]", transport));
                        final Collection<ModbusTransport> transportsToRemove = new ArrayList<>();

                        for(ModbusTransport currentTransport: clientTransports) {
                            if (currentTransport.equals(transport)) {
                                try {
                                    log.debug("Closing socket: " + currentTransport);
                                    currentTransport.close();
                                } catch (IOException e) {
                                    log.error(String.format("Socket [%s] closing error!", currentTransport), e);
                                }
                            }
                            if (!isTransportOpen(currentTransport)) {
                                transportsToRemove.add(currentTransport);
                            }
                        }

                        for(ModbusTransport removeTransport : transportsToRemove) {
                            clientTransports.remove(removeTransport);
                        }

                        clientTransports.add(transport);
                        log.debug("Sockets list: " + clientTransports);
                    }

                } catch (IOException e) {
                    log.error("Server socket exception: ", e);
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.debug("Server thread interrupted!, e");
                }
            }
        } catch (IOException e) {
            log.error("Server socket starting failed!", e);
        }

        for(ModbusTransport transport: clientTransports) {
            try {
                log.debug("Closing socket: " + transport);
                transport.close();
            } catch (IOException e) {
                log.error(String.format("Socket [%s] closing error!", transport), e);
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Server socket closing error!", e);
            }
        }
    }

    /**
     * Stops handling connections.
     */
    public void stopSocketsHandler() {working = false;}
}
