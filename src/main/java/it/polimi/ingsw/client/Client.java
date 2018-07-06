package it.polimi.ingsw.client;

import it.polimi.ingsw.client.gui.ClientGUIApplication;
import it.polimi.ingsw.shared.util.*;
import javafx.application.Application;
import joptsimple.*;
import java.io.IOException;
import java.util.*;


public class Client {

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

    Client(boolean debug) {
        this(debug, true);
    }

    private Client(boolean debug, boolean doNetworkSetup) {
        Logger.setDebug(debug);
        if (doNetworkSetup) {
            try {
                ClientNetwork.getInstance().setup();
                Logger.println("Connection established");
            } catch (IOException e) {
                Logger.error("Connection failed");
                Logger.printStackTraceConditionally(e);
                System.exit(Constants.EXIT_ERROR);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted() {
        this.gameStarted = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }

    public void setActive(String activeNickname) {
        setActive(activeNickname.equals(nickname));
        this.activeNickname = activeNickname;
    }

    public boolean isPatternChosen() {
        return patternChosen;
    }

    public void setPatternChosen(boolean value) {
        this.patternChosen = value;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public List<String> getSuspendedPlayers() {
        return suspendedPlayers;
    }

    public void setSuspendedPlayers(List<String> players) {
        suspendedPlayers = players;
        setSuspended(suspendedPlayers.contains(getNickname()));
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    String getActiveNickname() {
        return activeNickname;
    }

    public void reset() {
        nickname = null;
        uuid = null;
        logged = false;
        gameStarted = false;
        active = false;
        patternChosen = false;
        suspended = false;
        gameOver = false;
        activeNickname = null;
        suspendedPlayers = new ArrayList<>();
    }

    public static boolean isValidHost(String host){
        String ipRegex= "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String urlRegex = "^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return host.matches(ipRegex) || host.matches(urlRegex);
    }

    private static void exitError() {
        Logger.println(InterfaceMessages.CLIENT_USAGE_STRING);
        System.exit(Constants.EXIT_ERROR);
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(CLIArguments.DEBUG);
        parser.accepts(CLIArguments.HOST).withRequiredArg().defaultsTo(Constants.DEFAULT_HOST);
        parser.accepts(CLIArguments.PORT).withRequiredArg().ofType(Integer.class).defaultsTo(Constants.DEFAULT_PORT);
        parser.accepts(CLIArguments.CONNECTION).withRequiredArg().defaultsTo(CLIArguments.SOCKET);
        parser.accepts(CLIArguments.INTERFACE).withRequiredArg().defaultsTo(CLIArguments.GUI);

        try {
            OptionSet options = parser.parse(args);

            boolean debug = options.has(CLIArguments.DEBUG);

            String iface = (String) options.valueOf(CLIArguments.INTERFACE);
            switch (iface) {
                case CLIArguments.CLI:
                    String host = null;
                    host = (String) options.valueOf(CLIArguments.HOST);
                    if (!isValidHost(host)) {
                        Logger.println("Invalid Host");
                        exitError();
                    }
                    int port = (int) options.valueOf(CLIArguments.PORT);
                    String connection = (String) options.valueOf(CLIArguments.CONNECTION);
                    switch (connection) {
                        case CLIArguments.SOCKET:
                            ClientNetwork.setInstance(new SocketClient(host, port, debug));
                            break;
                        case CLIArguments.RMI:
                            port = Constants.DEFAULT_RMI_PORT;
                            ClientNetwork.setInstance(new RMIClient(host, port, debug));
                            break;
                        default:
                            Logger.println("Invalid type of connection");
                            exitError();
                            break;
                    }
                    new ClientCLI(debug).start();
                    break;
                case CLIArguments.GUI:
                    Client client = new Client(debug, false);
                    ClientGUIApplication.setClient(client);
                    ClientGUIApplication.setDebug(debug);
                    Application.launch(ClientGUIApplication.class);
                    break;
                default:
                    Logger.println("Invalid type of interface");
                    exitError();
                    break;
            }

        } catch (OptionException e) {
            Logger.printStackTraceConditionally(e);
            exitError();
        }
    }

}
