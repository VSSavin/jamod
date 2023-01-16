package net.wimpi.modbus.io;

import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.msg.ModbusMessage;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;

import java.io.IOException;
import java.net.Socket;

/**
 * @author vssavin on 16.01.2023
 */
public class ModbusTCPASCIITransport extends ModbusTCPTransport{
    private ModbusASCIITransport modbusASCIITransport;

    /**
     * Constructs a new <tt>ModbusTransport</tt> instance,
     * for a given <tt>Socket</tt>.
     * <p>
     *
     * @param socket the <tt>Socket</tt> used for message transport.
     */
    public ModbusTCPASCIITransport(Socket socket) {
        super(socket);
    }

    @Override
    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
        modbusASCIITransport.writeMessage(msg);
    }

    @Override
    public ModbusRequest readRequest() throws ModbusIOException {
        return modbusASCIITransport.readRequest();
    }

    @Override
    public ModbusResponse readResponse() throws ModbusIOException {
        return modbusASCIITransport.readResponse();
    }

    @Override
    protected void prepareStreams(Socket socket) throws IOException {
        if (modbusASCIITransport == null) modbusASCIITransport = new ModbusASCIITransport();
        modbusASCIITransport.prepareStreams(socket.getInputStream(), socket.getOutputStream());
    }
}
