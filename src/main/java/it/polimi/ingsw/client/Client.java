package it.polimi.ingsw.client;

import joptsimple.*;
import java.io.IOException;
import java.util.*;


public abstract class Client implements Observer {

    private static final String USAGE_STRING = "usage: sagradaclient [--debug] [--ip IP] [--port PORT] [--connection socket|rmi] [--interface cli|gui]";

    private String nickname;
    private UUID uuid;
    private ClientNetwork network;
    private boolean debugActive;
    private boolean logged = false;

    Client(ClientNetwork network, boolean debugActive) {
        this.debugActive = debugActive;
        this.network = network;
        this.network.addObserver(this);
        try {
            network.setup();
            log("Connection established");
        } catch (IOException e) {
            error("Connection failed");
        }
    }

    String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    UUID getUUID() {
        return uuid;
    }

    void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    ClientNetwork getNetwork() {
        return network;
    }

    boolean isDebugActive() {
        return debugActive;
    }

    boolean isLogged() {
        return logged;
    }

    void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * this method is used to print standard messages
     *
     * @param message that we want to print
     */
    static void log(String message) {
        System.out.println(message);
    }

    /**
     * this method is used to print messages intended for debugging
     *
     * @param message that we want to print out
     */
    void debug(String message) {
        if (this.isDebugActive())
            System.out.println("[DEBUG] " + message);
    }

    /**
     * this method is used to print error messages
     *
     * @param message that we want to print out
     */
    static void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    static boolean isValidIp(String ip){
        String ipPattern= "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(ipPattern);
    }

    abstract void start();

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
                if (!isValidIp(ip)) {
                    System.out.println("Invalid IP Address");
                    System.out.println(USAGE_STRING);
                }
            } else {
                Scanner stdin = new Scanner(System.in);
                do{
                    System.out.print("Inserisci un indirizzo IP valido\nIP >>> ");
                    ip = stdin.nextLine();
                } while(!isValidIp(ip));
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

            ClientNetwork socketClient = new SocketClient(ip, port, options.has("debug"));
            Client client = new ClientCLI(socketClient, options.has("debug"));
            client.start();

        } catch (OptionException e) {
            System.out.println(USAGE_STRING);
        }
    }

}
