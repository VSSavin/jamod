package net.wimpi.modbus.cmd;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ASCIIOverTCPSlaveConnectionFactory;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.*;

import java.net.Inet4Address;

/**
 * @author vssavin on 16.01.2023
 */
public class ASCIIOverTCPSlaveTest {

    public static void main(String[] args) {
        ModbusTCPListener listener = null;
        SimpleProcessImage spi = null;
        int port = Modbus.DEFAULT_PORT;

        try {
            if (args != null && args.length == 1) {
                port = Integer.parseInt(args[0]);
            }
            System.out.println("jModbus Modbus ASCII over TCP Slave (Server)");

            // 1. prepare a process image
            spi = new SimpleProcessImage();

            /*spi.addDigitalOut(new SimpleDigitalOut(true));
            spi.addDigitalOut(new SimpleDigitalOut(true));

            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            // allow checking LSB/MSB order
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            spi.addRegister(new SimpleRegister(251));*/

            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(45));

            // 2. create the coupler holding the image
            ModbusCoupler.getReference().setProcessImage(spi);
            ModbusCoupler.getReference().setMaster(false);
            ModbusCoupler.getReference().setUnitID(10);

            if (Modbus.debug) {
                System.out.println("Listening...");
            }
            // 3. create a listener with 3 threads in pool
            listener = new ModbusTCPListener(3, new ASCIIOverTCPSlaveConnectionFactory());
            listener.setPort(port);
            listener.setAddress(Inet4Address.getByName("127.0.0.1"));
            listener.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }// main
}
