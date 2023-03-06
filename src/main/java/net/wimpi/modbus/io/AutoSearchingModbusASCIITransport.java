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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class that implements the <tt>AutoSearchingModbusTransport</tt>
 * flavor.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public class AutoSearchingModbusASCIITransport extends AutoSearchingModbusTransport{
    private static final Logger log = LoggerFactory.getLogger(AutoSearchingModbusASCIITransport.class);

    private static final String SEARCH_REGISTER_PROP_NAME = "AutoSearchingModbusASCIITransport.searchDeviceRegister";
    private static final String SEARCH_TIMEOUT_PROP_NAME = "AutoSearchingModbusASCIITransport.searchTimeoutMs";
    private static final Integer DEFAULT_SEARCH_REGISTER_VALUE = 10;
    private static final Integer DEFAULT_SEARCH_TIMEOUT_VALUE = 5000;

    private static final int searchDeviceRegister;
    private static final int searchTimeoutMs;

    static {
        String searchRegister = System.getProperty(SEARCH_REGISTER_PROP_NAME);
        String searchTimeout = System.getProperty(SEARCH_TIMEOUT_PROP_NAME);

        Integer reg;
        if (searchRegister != null) {
            try {
                reg = Integer.parseInt(searchRegister);
            } catch (Exception e) {
                log.error("Search register init error!", e);
                reg = DEFAULT_SEARCH_REGISTER_VALUE;
            }
        } else {
            Properties props = new Properties();
            try {
                props.load(new FileReader("conf.properties"));
                String prop = props.getProperty(SEARCH_REGISTER_PROP_NAME);
                if (prop != null) reg = Integer.parseInt(prop);
                else reg = DEFAULT_SEARCH_REGISTER_VALUE;
            } catch (FileNotFoundException e) {
                log.warn("File conf.properties not found!");
                reg = DEFAULT_SEARCH_REGISTER_VALUE;
            }
            catch (IOException | NumberFormatException e) {
                log.warn("Processing conf.properties error!", e);
                reg = DEFAULT_SEARCH_REGISTER_VALUE;
            }
        }

        searchDeviceRegister = reg;

        Integer timeout;
        if (searchTimeout != null) {
            try {
                timeout = Integer.parseInt(searchTimeout);
            } catch (Exception e) {
                log.error("Search timeout init error!", e);
                timeout = DEFAULT_SEARCH_TIMEOUT_VALUE;
            }
        }
        else {
            Properties props = new Properties();
            try {
                props.load(new FileReader("conf.properties"));
                String prop = props.getProperty(SEARCH_TIMEOUT_PROP_NAME);
                if (prop != null) timeout = Integer.parseInt(prop);
                else timeout = DEFAULT_SEARCH_REGISTER_VALUE;
            } catch (IOException | NumberFormatException e) {
                log.error("Processing conf.properties error!", e);
                timeout = DEFAULT_SEARCH_TIMEOUT_VALUE;
            }
        }

        searchTimeoutMs = timeout;
    }

    private final int overrideSearchRegister;
    private final int overrideSearchTimeout;
    private final boolean searchingAllowed;

    /**
     * Constructs a new <tt>AutoSearchingModbusASCIITransport</tt> instance.
     *
     * @param streamTransport the <tt>StreamTransport</tt> used to access streams.
     * @param searchUnitIds the <tt>Collection<Integer></tt> used to find unit ids.
     */
    public AutoSearchingModbusASCIITransport(StreamTransport streamTransport, Collection<Integer> searchUnitIds) {
        super(streamTransport, searchUnitIds);
        this.name = streamTransport.toString();
        this.overrideSearchRegister = -1;
        this.overrideSearchTimeout = -1;
        searchingAllowed = true;
        units = searchUnits();
    }

    /**
     * Constructs a new <tt>AutoSearchingModbusASCIITransport</tt> instance.
     *
     * @param streamTransport the <tt>StreamTransport</tt> used to access streams.
     * @param modbusSearchRegister a modbus register address to find units.
     *                             Overrides the default value and the system loaded value.
     *                             Ignored if the value is <tt>-1</tt>.
     * @param searchUnitIds the <tt>Collection<Integer></tt> used to find unit ids.
     */
    public AutoSearchingModbusASCIITransport(StreamTransport streamTransport, int modbusSearchRegister,
                                             Collection<Integer> searchUnitIds) {
        super(streamTransport, searchUnitIds);
        this.name = streamTransport.toString();
        this.overrideSearchRegister = modbusSearchRegister;
        this.overrideSearchTimeout = -1;
        searchingAllowed = true;
        units = searchUnits();
    }

    /**
     * Constructs a new <tt>AutoSearchingModbusASCIITransport</tt> instance.
     *
     * @param streamTransport the <tt>StreamTransport</tt> used to access streams.
     * @param modbusSearchRegister a modbus register address to find units.
     *                             Overrides the default value and the system loaded value.
     *                             Ignored if the value is <tt>-1</tt>.
     * @param modbusSearchTimeout timeout in milliseconds to find units.
     * @param searchUnitIds the <tt>Collection<Integer></tt> used to find unit ids.
     */
    public AutoSearchingModbusASCIITransport(StreamTransport streamTransport, int modbusSearchRegister,
                                             int modbusSearchTimeout, Collection<Integer> searchUnitIds) {
        super(streamTransport, searchUnitIds);
        this.name = streamTransport.toString();
        this.overrideSearchRegister = modbusSearchRegister;
        this.overrideSearchTimeout = modbusSearchTimeout;
        searchingAllowed = true;
        units = searchUnits();
    }

    @Override
    protected ModbusTransport initModbusTransport() {
        ModbusASCIITransport transport = new ModbusASCIITransport();
        try {
            transport.prepareStreams(getStreamTransport().getInputStream(), getStreamTransport().getOutputStream());
        } catch (IOException e) {
            String errorMessage = "ModbusASCIITransport initialize error: ";
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
        return transport;
    }

    @Override
    public void setTimeout(int timeoutMs) {
        try {
            getStreamTransport().setTimeout(timeoutMs);
        } catch (Exception e) {
            String errorMessage = "Set socket timeout error: ";
            log.error(errorMessage, e);
            throw new AutoSearchingModbusTransportException(errorMessage, e);
        }
    }

    @Override
    protected Collection<Integer> searchUnits() {
        Collection<Integer> socketUnits = new HashSet<>();
        if (searchingAllowed) {
            Collection<Integer> unitIds = getSearchUnitIds();
            for(Integer devAddr: unitIds) {
                ReadInputRegistersRequest request = new ReadInputRegistersRequest();
                request.setHeadless();
                request.setUnitID(devAddr);
                request.setReference(overrideSearchRegister > -1 ? overrideSearchRegister : searchDeviceRegister);
                request.setWordCount(1);
                try {
                    getStreamTransport().setTimeout(overrideSearchTimeout > -1 ? overrideSearchTimeout: searchTimeoutMs);
                    log.debug(String.format("[%s]: %s", new Date(), "Writing request " + "[" + request.getHexMessage() + "]"));
                    getTransport().writeMessage(request);
                    ModbusResponse response = getTransport().readResponse();
                    socketUnits.add(response.getUnitID());
                } catch (ModbusIOException e) {
                    log.error(String.format("Modbus processing error [request: %s]: ", request.getHexMessage()), e);
                    try {
                        getTransport().close();
                    } catch (IOException ex) {
                        log.error("Transport close error: ", e);
                    }
                } catch (Exception e) {
                    log.error("Client socket write error: ", e);
                }
            }
        }

        return socketUnits;
    }

    @Override
    public boolean isOpen() {
        return getStreamTransport().isOpen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutoSearchingModbusASCIITransport that = (AutoSearchingModbusASCIITransport) o;

        return units.equals(that.units);
    }

    @Override
    public int hashCode() {
        return units.hashCode();
    }
}
