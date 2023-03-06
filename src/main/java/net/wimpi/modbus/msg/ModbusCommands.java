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

package net.wimpi.modbus.msg;

import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.util.BitVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface defining a <tt>ModbusCommands</tt> class,
 * containing all available modbus commands.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public interface ModbusCommands {
    /**
     * This command is used to read from 1 to 2000
     * contiguous status of coils in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the register of the coil.
     * @param count the count of the coils.
     * @return the <tt>BitVector</tt> that stores
     * the collection of bits that have been read.
     */
    BitVector readCoils(int unitId, int ref, int count);

    /**
     * This command is used to read from 1 to 2000
     * contiguous status of discrete inputs in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the register.
     * @param count the count of the discrete inputs.
     * @return the <tt>BitVector</tt> that stores
     * the collection of bits that have been read.
     */
    BitVector readDiscreteInputs(int unitId, int ref, int count);

    /**
     * This command is used to read the contents of a contiguous block
     * of holding registers in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the holding register.
     * @param count the count of the holding registers.
     * @return the <tt>BitVector</tt> that stores
     * the collection of bits that have been read.
     */
    Register[] readHoldingRegisters(int unitId, int ref, int count);

    /**
     * This command is used to read from 1 to 125
     * contiguous input registers in a remote device
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the input register.
     * @param count the count of the input registers.
     * @return the <tt>BitVector</tt> that stores
     * the collection of bits that have been read.
     */
    InputRegister[] readInputRegisters(int unitId, int ref, int count);

    /**
     * This command is used to write a single output
     * to either ON or OFF in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the coil.
     * @param state the state of the coil.
     * @return true if set, false otherwise.
     */
    boolean writeSingleCoil(int unitId, int ref, boolean state);

    /**
     * This function code is used to write
     * a single holding register in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the register.
     * @param register Register to be written.
     */
    void writeSingleRegister(int unitId, int ref, Register register);

    /**
     * This command is used to read the contents
     * of eight Exception Status outputs in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @return the<tt>int</tt> that stores the unit exception status.
     */
    int readExceptionStatus(int unitId);

    /**
     * This command provides a series of tests for checking
     * the communication system between a client (Master) device
     * and a server ( Slave), or for checking various internal error
     * conditions within a server.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param subFunction diagnostic sub function
     * @param data data to be written.
     * @return the unit diagnostic data.
     */
    Register[] diagnostics(int unitId, int subFunction, Register[] data);

    /**
     * This command is used to get a status word and an event count from
     * the remote device's communication event counter.
     *
     * @param unitId the unit identifier.
     * @return the status word and the event count word.
     */
    Register[] getCommEventCounter(int unitId);

    /**
     * This command is used to get a status word, event count,
     * message count, and a field of event bytes from the remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @return the status word, event count, message count and field of event bytes
     */
    Register[] getCommEventLog(int unitId);

    /**
     * This command is used to force each coil in a sequence of
     * coils to either ON or OFF in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the first coil.
     * @param coils the <tt>BitVector</tt> that stores
     * the collection of bits that will be written.
     */
    void writeMultipleCoils(int unitId, int ref, BitVector coils);

    /**
     * This command is used to write a block of contiguous registers
     * (1 to 123 registers) in a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param ref the reference of the first register.
     * @param registers the <tt>Register[]</tt> that stores register array that will be written.
     */
    void writeMultipleRegisters(int unitId, int ref, Register[] registers);

    /**
     * This command is used to read the description of the type, the current status, and other
     * information specific to a remote device.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @return any information specific to a remote device.
     */
    Register[] reportSlaveId(int unitId);

    /**
     * This command is used to perform a file record read. All Request Data Lengths are
     * provided in terms of number of bytes and all Record Lengths are provided in terms of
     * registers.
     * @see <a href="https://modbus.org/docs/Modbus_Application_Protocol_V1_1b.pdf">Modbus application protocol</a>
     *
     * @param unitId the unit identifier.
     * @param recordId the identifier of the record.
     * @return the <tt>ModbusFile</tt> that stores record data.
     */
    ModbusFile readFileRecord(int unitId, int recordId);

    /**
     * Class that implements a ModbusFile.
     *
     * @author vssavin
     * @version @version@ (@date@)
     */
    class ModbusFile {
        private final int unitId;
        private final int fileNumber;
        private final List<Register[]> recordDataList = new ArrayList<>();

        /**
         * Constructs a new <tt>ModbusFile</tt> instance.
         *
         * @param unitId the unit identifier.
         * @param fileNumber the number of the file.
         */
        public ModbusFile(int unitId, int fileNumber) {
            this.unitId = unitId;
            this.fileNumber = fileNumber;
        }

        /**
         * Adds the record data to the file.
         *
         * @param recordData the record data to be added.
         */
        public void addRecord(Register[] recordData) {
            recordDataList.add(recordData);
        }

        /**
         * Method returns the data of the specified record.
         *
         * @param recordId the identifier of the record
         * @return the data of the record
         */
        public Register[] getRecordData(int recordId) {
            if (recordId > recordDataList.size() - 1)
                throw new IllegalArgumentException(
                        String.format("Record [%s] for file [%s] not found!", recordId, fileNumber));
            return recordDataList.get(recordId);
        }

        /**
         * Method returns the file number.
         *
         * @return the file number.
         */
        public int getFileNumber() {
            return fileNumber;
        }

        /**
         * Method returns the unit identifier.
         *
         * @return the unit identifier.
         */
        public int getUnitId() {
            return unitId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ModbusFile that = (ModbusFile) o;

            if (unitId != that.unitId) return false;
            return fileNumber == that.fileNumber;
        }

        @Override
        public int hashCode() {
            int result = unitId;
            result = 31 * result + fileNumber;
            return result;
        }
    }

}
