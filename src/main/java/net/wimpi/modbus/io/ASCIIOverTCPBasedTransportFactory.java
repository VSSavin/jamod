package net.wimpi.modbus.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * @author vssavin on 17.01.2023
 */
public class ASCIIOverTCPBasedTransportFactory implements ModbusSocketBasedTransportFactory {
    private static final Logger logger = LoggerFactory.getLogger(ASCIIOverTCPBasedTransportFactory.class);

    @Override
    public ModbusTransport create(Socket socket) {
        ModbusASCIITransport transport = new ModbusASCIITransport();
        try {
            transport.prepareStreams(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            logger.error("Preparing transport streams error!", e);
        }
        return transport;
    }
}
