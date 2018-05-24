package it.polimi.ingsw.client;

import joptsimple.*;
import java.util.Scanner;


public class Client {

    private static final String USAGE_STRING = "usage: sagradaclient [--debug] [--ip IP] [--port PORT] [--connection socket|rmi] [--interface cli|gui]";

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("debug");
        parser.accepts("ip").withRequiredArg();
        parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(42000);
        parser.accepts("connection").withRequiredArg().ofType(String.class).defaultsTo("socket");
        parser.accepts("interface").withRequiredArg().ofType(String.class).defaultsTo("cli");

        try {
            OptionSet options = parser.parse(args);

            String ip;
            if (options.has("ip")) {
                ip = (String) options.valueOf("ip");
            } else {
                Scanner stdin = new Scanner(System.in);
                System.out.print("IP >>> ");
                ip = stdin.nextLine();
            }

            int port = (int) options.valueOf("port");

            String connection = (String) options.valueOf("connection");
            if (!(connection.equals("socket") || connection.equals("rmi"))) {
                System.out.println("Invalid type of connection");
                System.out.println(USAGE_STRING);
            }

            String iface = (String) options.valueOf("interface");
            if (!(iface.equals("cli") || iface.equals("gui"))) {
                System.out.println("Invalid type of interface");
                System.out.println(USAGE_STRING);
            }

            SocketClient client = new SocketClient(ip, port, options.has("debug"));
            client.startClient();

        } catch (OptionException e) {
            System.out.println(USAGE_STRING);
        }
    }

}
