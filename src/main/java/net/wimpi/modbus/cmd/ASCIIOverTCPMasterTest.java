package net.wimpi.modbus.cmd;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;

/**
 * @author vssavin on 17.01.2023
 */
public class ASCIIOverTCPMasterTest {

    static {
        System.setProperty("net.wimpi.modbus.debug", "true");
    }

    public static void main(String[] args) {

        InetAddress addr = null;
        TCPMasterConnection con = null;
        ModbusRequest req = null;
        ModbusTransaction trans = null;
        int ref = 0;
        int count = 0;
        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 1. Setup parameters
            if (args.length < 3) {
                printUsage();
                System.exit(1);
            } else {
                try {
                    String astr = args[0];
                    int idx = astr.indexOf(':');
                    if (idx > 0) {
                        port = Integer.parseInt(astr.substring(idx + 1));
                        astr = astr.substring(0, idx);
                    }
                    addr = InetAddress.getByName(astr);
                    ref = Integer.parseInt(args[1]);
                    count = Integer.parseInt(args[2]);

                    if (args.length == 4) {
                        repeat = Integer.parseInt(args[3]);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    printUsage();
                    System.exit(1);
                }
            }
            // 2. Open the connection
            con = new TCPMasterConnection(addr, port, 5000, TCPMasterConnection.ModbusEncoding.ASCII);
            con.setPort(port);
            con.connect();

            if (Modbus.debug) {
                System.out.println("Connected to " + addr.toString() + ":" + con.getPort());
            }
            req = new ReadInputRegistersRequest(ref, 1);
            req.setUnitID(10);
            if (Modbus.debug) {
                System.out.println("Request: " + req.getHexMessage());
            }

            // 3. Prepare the transaction
            trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);

            // 4. Execute the transaction repeat times
            int k = 0;
            do {
                trans.execute();

                if (Modbus.debug) {
                    System.out.println("Response: " + trans.getResponse().getHexMessage());
                }

                k++;
            } while (k < repeat);

            // 5. Close the connection
            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }// main

    private static void printUsage() {
        System.out.println(
                "java net.wimpi.modbus.cmd.ModbusMaster <address{:<port>} [String]> <register [int]> <value [int]> {<repeat [int]>}");
    }
}
