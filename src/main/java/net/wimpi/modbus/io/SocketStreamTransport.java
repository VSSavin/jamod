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

package net.wimpi.modbus.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class that implements the <tt>StreamTransport</tt> transport flavor.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public class SocketStreamTransport implements StreamTransport{
    private static final Logger log = LoggerFactory.getLogger(SocketStreamTransport.class);
    private final Socket socket;

    /**
     * Constructs a new <tt>SocketStreamTransport</tt> instance,
     * for a given <tt>Socket</tt>.
     * @param socket the <tt>Socket</tt> used for message transport.
     */
    public SocketStreamTransport(Socket socket) {
        if (socket == null) throw new IllegalStateException("Socket can't be null!");
        this.socket = socket;
    }


    @Override
    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            String errorMessage = "Getting input stream error: ";
            log.error(errorMessage, e);
            throw new StreamTransportException(errorMessage, e);
        }
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            String errorMessage = "Getting input stream error: ";
            log.error(errorMessage, e);
            throw new StreamTransportException(errorMessage, e);
        }
    }

    @Override
    public void setTimeout(int timeoutMs) {
        try {
            socket.setSoTimeout(timeoutMs);
        } catch (IOException e) {
            String errorMessage = "Getting input stream error: ";
            log.error(errorMessage, e);
            throw new StreamTransportException(errorMessage, e);
        }
    }

    @Override
    public boolean isOpen() {
        return !socket.isClosed();
    }

    @Override
    public String toString() {
        String ret = String.format("SocketStreamTransport[%s:%s]", socket.getLocalAddress().toString(),
                socket.getPort());
        return isOpen() ? ret  : "SocketStreamTransport[unconnected]";
    }
}
