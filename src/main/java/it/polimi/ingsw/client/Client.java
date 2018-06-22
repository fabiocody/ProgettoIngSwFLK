package it.polimi.ingsw.client;

import it.polimi.ingsw.shared.util.*;
import javafx.application.Application;
import joptsimple.*;
import java.io.IOException;
import java.util.*;


public abstract class Client implements Observer {

    private String nickname;
    private UUID uuid;
    private boolean logged = false;
    private boolean gameStarted = false;
    private boolean active = false;
    private boolean patternChosen = false;
    private boolean suspended = false;
    private boolean gameOver = false;
    private String activeNickname = null;
    private List<String> suspendedPlayers = new ArrayList<>();
    private int favorTokens = 0;

    Client(boolean debugActive) {
        Logger.setDebugActive(debugActive);
        ClientNetwork.getInstance().addObserver(this);
        try {
            ClientNetwork.getInstance().setup();
            Logger.println("Connection established");
        } catch (IOException e) {
            Logger.error("Connection failed");
            e.printStackTrace();
            System.exit(Constants.EXIT_ERROR);
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

    boolean isLogged() {
        return logged;
    }

    void setLogged(boolean logged) {
        this.logged = logged;
    }

    boolean isGameStarted() {
        return gameStarted;
    }

    void setGameStarted() {
        this.gameStarted = true;
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
    }

    boolean isPatternChosen() {
        return patternChosen;
    }

    void setPatternChosen() {
        this.patternChosen = true;
    }

    boolean isSuspended() {
        return suspended;
    }

    void setSuspended(List<String> suspendedPlayers) {
        this.suspended = suspendedPlayers.contains(this.getNickname());
        this.suspendedPlayers = suspendedPlayers;
    }

    boolean isGameOver() {
        return gameOver;
    }

    void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    String getActiveNickname() {
        return activeNickname;
    }
      
    public int getFavorTokens() {
        return favorTokens;
    }

    void setFavorTokens(int favorTokens) {
        this.favorTokens = favorTokens;
    }

    List<String> getSuspendedPlayers() {
        return suspendedPlayers;
    }

    private static boolean isValidHost(String host){
        String ipRegex= "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String urlRegex = "^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return host.matches(ipRegex) || host.matches(urlRegex);
    }

    abstract void start();

    private static void exitError() {
        Logger.println(Constants.CLIENT_USAGE_STRING);
        System.exit(Constants.EXIT_ERROR);
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(CLIArguments.DEBUG);
        parser.accepts(CLIArguments.HOST).withRequiredArg();
        parser.accepts(CLIArguments.PORT).withRequiredArg().ofType(Integer.class).defaultsTo(Constants.DEFAULT_PORT);
        parser.accepts(CLIArguments.CONNECTION).withRequiredArg().ofType(String.class).defaultsTo(CLIArguments.SOCKET);
        parser.accepts(CLIArguments.INTERFACE).withRequiredArg().ofType(String.class).defaultsTo(CLIArguments.CLI);

        try {
            OptionSet options = parser.parse(args);

            boolean debug = options.has(CLIArguments.DEBUG);

            String host;
            if (options.has(CLIArguments.HOST)) {
                host = (String) options.valueOf(CLIArguments.HOST);
                if (!isValidHost(host)) {
                    Logger.println("Invalid Host");
                    exitError();
                }
            } else {
                Scanner stdin = new Scanner(System.in);
                do {
                    Logger.print("Inserisci un host valido\nHost >>> ");
                    host = stdin.nextLine();
                } while (!isValidHost(host));
                stdin.close();
            }

            int port = (int) options.valueOf(CLIArguments.PORT);

            String connection = (String) options.valueOf(CLIArguments.CONNECTION);
            switch (connection) {
                case CLIArguments.SOCKET:
                    ClientNetwork.setInstance(new SocketClient(host, port, debug));
                    break;
                case CLIArguments.RMI:
                    ClientNetwork.setInstance(new RMIClient(host, port, debug));
                    break;
                default:
                    Logger.println("Invalid type of connection");
                    exitError();
                    break;
            }

            String iface = (String) options.valueOf(CLIArguments.INTERFACE);
            switch (iface) {
                case CLIArguments.CLI:
                    new ClientCLI(debug).start();
                    break;
                case CLIArguments.GUI:
                    Application.launch(ClientGUIApplication.class);
                    break;
                default:
                    Logger.println("Invalid type of interface");
                    exitError();
                    break;
            }

        } catch (OptionException e) {
            //e.printStackTrace();
            exitError();
        }
    }

}
