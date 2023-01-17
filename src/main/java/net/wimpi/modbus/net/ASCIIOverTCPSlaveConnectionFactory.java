package net.wimpi.modbus.net;

import net.wimpi.modbus.io.ASCIIOverTCPBasedTransportFactory;

import java.net.Socket;

/**
 * @author vssavin on 17.01.2023
 */
public class ASCIIOverTCPSlaveConnectionFactory implements TCPSlaveConnectionFactory {

    @Override
    public TCPSlaveConnection create(Socket socket) {
        return new TCPSlaveConnection(socket, new ASCIIOverTCPBasedTransportFactory());
    }

}
