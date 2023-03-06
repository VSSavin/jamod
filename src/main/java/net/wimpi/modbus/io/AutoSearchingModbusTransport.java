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

import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.util.BitVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Abstract base class for <tt>ModbusTransport</tt>
 * and <tt>ModbusCommands</tt> implementations.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public abstract class AutoSearchingModbusTransport implements ModbusTransport, ModbusCommands {
    private static final Logger log = LoggerFactory.getLogger(AutoSearchingModbusTransport.class);
    protected String name = this.getClass().getSimpleName();
    protected Collection<Integer> units;
    private final Collection<Integer> searchUnitIds;
    private final StreamTransport streamTransport;
    private final ModbusTransport transport;

    /**
     * Constructs a new <tt>SocketStreamTransport</tt> instance,
     * for a given <tt>StreamTransport</tt> and a <tt>Collection<Integer></tt>.
     * @param streamTransport the <tt>StreamTransport</tt> used to access streams.
     * @param searchUnitIds the <tt>Collection<Integer></tt> used to find unit ids.
     */
    public AutoSearchingModbusTransport(StreamTransport streamTransport, Collection<Integer> searchUnitIds) {
        if (streamTransport == null) throw new IllegalStateException("Stream transport can't be null!");
        this.streamTransport = streamTransport;
        this.searchUnitIds = searchUnitIds;
        transport = initModbusTransport();
        units = searchUnits();
    }

    /**
     * Initializes some <tt>ModbusTransport</tt>.
     *
     * @return a <tt>ModbusTransport</tt> implementation.
     */
    protected abstract ModbusTransport initModbusTransport();

    /**
     * Searching unit ids from <tt>searchUnitIds</tt> field.
     *
     * @return a <tt>Collection<Integer></tt> with found unit ids.
     */
    protected abstract Collection<Integer> searchUnits();

    /**
     * Sets a timeout value in milliseconds.
     *
     * @param timeoutMs an <tt>int</tt> timeout value in milliseconds.
     */
    public abstract void setTimeout(int timeoutMs);

    /**
     * Returns open state of transport.
     *
     * @return true if the transport is open, false otherwise.
     */
    public abstract boolean isOpen();

    /**
     * Returns the <tt>StreamTransport</tt>.
     *
     * @return a <tt>StreamTransport</tt> value.
     */
    protected StreamTransport getStreamTransport() {
        return streamTransport;
    }

    /**
     * Returns the <tt>Collection<Integer></tt> with unit ids to find.
     *
     * @return a <tt>Collection<Integer></tt> value.
     */
    protected Collection<Integer> getSearchUnitIds() {
        return searchUnitIds;
    }

    /**
     * Returns the <tt>ModbusTransport</tt>.
     *
     * @return a <tt>ModbusTransport</tt> value.
     */
    public ModbusTransport getTransport() {
        return transport;
    }

    /**
     * Returns found unit ids.
     *
     * @return a <tt>Collection<Integer></tt> with found unit ids.
     */
    public Collection<Integer> getUnits() {
        return units;
    }

    /**
     * Returns the name of the object.
     *
     * @return a <tt>String</tt> value.
     */
    public String getName() {
        return name;
    }

    @Override
    public BitVector readCoils(int unitId, int ref, int count) {
        ReadCoilsRequest request = new ReadCoilsRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setBitCount(count);

        try {
            writeMessage(request);
            ReadCoilsResponse response = (ReadCoilsResponse)readResponse();
            return response.getCoils();
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public BitVector readDiscreteInputs(int unitId, int ref, int count) {
        ReadInputDiscretesRequest request = new ReadInputDiscretesRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setBitCount(count);

        try {
            writeMessage(request);
            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse)readResponse();
            return response.getDiscretes();
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public Register[] readHoldingRegisters(int unitId, int ref, int count) {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setWordCount(count);

        try {
            writeMessage(request);
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)readResponse();
            return response.getRegisters();
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public InputRegister[] readInputRegisters(int unitId, int ref, int count) {
        ReadInputRegistersRequest request = new ReadInputRegistersRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setWordCount(count);

        try {
            writeMessage(request);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse)readResponse();
            return response.getRegisters();
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public boolean writeSingleCoil(int unitId, int ref, boolean state) {
        WriteCoilRequest request = new WriteCoilRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setCoil(state);
        try {
            writeMessage(request);
            WriteCoilResponse response = (WriteCoilResponse) readResponse();
            return response.getCoil();
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public void writeSingleRegister(int unitId, int ref, Register register) {
        WriteSingleRegisterRequest request = new WriteSingleRegisterRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setRegister(register);
        try {
            writeMessage(request);
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public int readExceptionStatus(int unitId) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Register[] diagnostics(int unitId, int subFunction, Register[] data) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Register[] getCommEventCounter(int unitId) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Register[] getCommEventLog(int unitId) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void writeMultipleCoils(int unitId, int ref, BitVector coils) {
        WriteMultipleCoilsRequest request = new WriteMultipleCoilsRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setCoils(coils);

        try {
            writeMessage(request);
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public void writeMultipleRegisters(int unitId, int ref, Register[] registers) {
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest();
        request.setUnitID(unitId);
        request.setReference(ref);
        request.setRegisters(registers);
        try {
            writeMessage(request);
        } catch (ModbusIOException e) {
            String errorMessage = String.format("Modbus processing error [request: %s]: ", request.getHexMessage());
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    public Register[] reportSlaveId(int unitId) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ModbusFile readFileRecord(int unitId, int recordId) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }

    @Override
    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
        transport.writeMessage(msg);
    }

    @Override
    public ModbusRequest readRequest() throws ModbusIOException {
        return transport.readRequest();
    }

    @Override
    public ModbusResponse readResponse() throws ModbusIOException {
        return transport.readResponse();
    }

    @Override
    public String toString() {
        return streamTransport.toString();
    }
}
