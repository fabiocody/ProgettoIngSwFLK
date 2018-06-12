package it.polimi.ingsw.client;

import it.polimi.ingsw.util.*;
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
    private boolean gameStarted = false;
    private boolean active = false;
    private boolean patternChosen = false;
    private boolean suspended = false;
    private boolean gameOver = false;
    private String activeNickname = null;

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

    boolean isGameStarted() {
        return gameStarted;
    }

    void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    boolean isActive() {
        return active;
    }

    void setActive(Boolean active){
        this.active = active;
    }

    void setActive(String activeNickname){
       this.active = activeNickname.equals(nickname);
       this.activeNickname = activeNickname;
       if (!this.active && !this.suspended) log("Aspetta il tuo turno.");
    }

    boolean isPatternChosen() {
        return patternChosen;
    }

    void setPatternChosen(boolean patternChosen) {
        this.patternChosen = patternChosen;
    }

    boolean isSuspended() {
        return suspended;
    }

    void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    boolean isGameOver() {
        return gameOver;
    }

    void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getActiveNickname() {
        return activeNickname;
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

    static boolean isValidHost(String host){
        String ipRegex= "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String urlRegex = "^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return host.matches(ipRegex) || host.matches(urlRegex);
    }

    abstract void start();

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(CLIArguments.DEBUG);
        parser.accepts(CLIArguments.HOST).withRequiredArg();
        parser.accepts(CLIArguments.PORT).withRequiredArg().ofType(Integer.class).defaultsTo(Constants.DEFAULT_PORT);
        parser.accepts(CLIArguments.CONNECTION).withRequiredArg().ofType(String.class).defaultsTo(CLIArguments.SOCKET);
        parser.accepts(CLIArguments.INTERFACE).withRequiredArg().ofType(String.class).defaultsTo(CLIArguments.CLI);

        try {
            OptionSet options = parser.parse(args);

            String host;
            if (options.has(CLIArguments.HOST)) {
                host = (String) options.valueOf(CLIArguments.HOST);
                if (!isValidHost(host)) {
                    System.out.println("Invalid Host");
                    System.out.println(USAGE_STRING);
                }
            } else {
                Scanner stdin = new Scanner(System.in);
                do{
                    System.out.print("Inserisci un host valido\nHost >>> ");
                    host = stdin.nextLine();
                } while(!isValidHost(host));
                stdin.close();
            }

            int port = (int) options.valueOf(CLIArguments.PORT);

            String connection = (String) options.valueOf(CLIArguments.CONNECTION);
            if (!(connection.equals(CLIArguments.SOCKET) || connection.equals(CLIArguments.RMI))) {
                System.out.println("Invalid type of connection");
                System.out.println(USAGE_STRING);
            }

            String iface = (String) options.valueOf(CLIArguments.INTERFACE);
            if (!(iface.equals(CLIArguments.CLI) || iface.equals(CLIArguments.GUI))) {
                System.out.println("Invalid type of interface");
                System.out.println(USAGE_STRING);
            }

            ClientNetwork socketClient = new SocketClient(host, port, options.has(CLIArguments.DEBUG));
            Client client = new ClientCLI(socketClient, options.has(CLIArguments.DEBUG));
            client.start();

        } catch (OptionException e) {
            System.out.println(USAGE_STRING);
        }
    }

}
