package net.wimpi.modbus.cmd;

import net.wimpi.modbus.io.*;
import net.wimpi.modbus.net.ClientSocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that implements a simple commandline
 * tool for automatically search for connected units.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public class AutoSearchingModbusASCIITransportTest {
    public static void main(String[] args) {
        String mode = args[0];
        String host = args[1];
        String port = args[2];
        Collection<Integer> units = new ArrayList<>();
        if (args.length > 3) {
            String[] unitIds = args[3].split(",");
            for(String id : unitIds) {
                try {
                    units.add(Integer.parseInt(id));
                }
                catch (NumberFormatException e) {
                    System.out.println("Wrong unit id value! Should be an integer value!");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        if (units.size() == 0) {
            units.add(1);
            units.add(2);
            units.add(3);
        }

        if (mode.toLowerCase().equals("client")) {
            //1. TCP client mode
            try {
                StreamTransport streamTransport = new SocketStreamTransport(new Socket(host, Integer.parseInt(port)));
                AutoSearchingModbusASCIITransport transport =
                        new AutoSearchingModbusASCIITransport(streamTransport, units);
                System.out.println(transport.getUnits());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (mode.toLowerCase().equals("server")) {
            //2. TCP server mode
            ModbusASCIIClientSocketHandler socketHandler = new ModbusASCIIClientSocketHandler(host, Integer.parseInt(port), units);
            new Thread(socketHandler).start();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(socketHandler.getClientTransportsInfo());
            socketHandler.stopSocketsHandler();
        }

    }

    private static class ModbusASCIIClientSocketHandler extends ClientSocketHandler {
        private final Collection<Integer> searchUnitIds;

        /**
         * Constructs a new <tt>ClientSocketHandler</tt> instance
         *
         * @param host the host to start handling connections.
         * @param port the port to start handling connections.
         */
        public ModbusASCIIClientSocketHandler(String host, int port, Collection<Integer> searchUnitIds) {
            super(host, port);
            this.searchUnitIds = searchUnitIds;
        }

        @Override
        protected ModbusTransport createTransport(StreamTransport streamTransport) {
            return new AutoSearchingModbusASCIITransport(streamTransport, searchUnitIds);
        }

        @Override
        protected boolean isTransportOpen(ModbusTransport transport) {
            return transport != null && ((AutoSearchingModbusASCIITransport) transport).isOpen();
        }

        public Collection<String> getClientTransportsInfo() {
            Collection<String> info = new ArrayList<>();
            for(ModbusTransport transport : clientTransports) {
                info.add("Transport units: " + ((AutoSearchingModbusASCIITransport)transport).getUnits());
            }
            return info;
        }
    }
}
