package it.polimi.ingsw.client;

import it.polimi.ingsw.client.gui.ClientGUI;
import it.polimi.ingsw.shared.util.*;
import javafx.application.Application;
import joptsimple.*;
import java.io.IOException;
import java.util.*;


/**
 * This is the base class for <code>ClientGUI</code> and <code>ClientCLI</code>
 * @author Team
 */
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

    /**
     * This constructor calls the default constructor with the <code>doNetworkSetup</code> parameter set true
     * @param debug whether or not to start the client in debug mode
     */
    Client(boolean debug) {
        this(debug, true);
    }

    /**
     * @param debug whether or not to start the client in debug mode
     * @param doNetworkSetup whether or not to set up the network
     */
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

    /**
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the player's UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @param uuid the UUID to set
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return whether or not the user is logged
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * @param logged whether or not to log the user
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * @return whether or not the game is started
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * This method sets the game as started
     */
    public void setGameStarted() {
        this.gameStarted = true;
    }

    /**
     * @return whether or not the user is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active whether or not to set the user as active
     */
    public void setActive(Boolean active){
        this.active = active;
    }

    /**
     * @param activeNickname the nickname of the user set active by the server
     */
    public void setActive(String activeNickname) {
        setActive(activeNickname.equals(nickname));
        this.activeNickname = activeNickname;
    }

    /**
     * @return whether or not a pattern has been chosen
     */
    public boolean isPatternChosen() {
        return patternChosen;
    }

    /**
     * @param value whether or not to set the pattern as chosen
     */
    public void setPatternChosen(boolean value) {
        this.patternChosen = value;
    }

    /**
     * @return whether or not the user is suspended
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * @param suspended whether or not to set the user as suspended
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * @return the list of suspended players
     */
    public List<String> getSuspendedPlayers() {
        return suspendedPlayers;
    }

    /**
     * @param players the player to be set as suspended
     */
    public void setSuspendedPlayers(List<String> players) {
        suspendedPlayers = players;
        setSuspended(suspendedPlayers.contains(getNickname()));
    }

    /**
     * @return whether the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @param gameOver
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * @return the nickname of the active player
     */
    public String getActiveNickname() {
        return activeNickname;
    }


    /**
     * This method resets the <code>Client</code> to the starting state
     */
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

    /**
     * @param host the IP or URL of the server
     * @return whether or not the IP or URL is valid
     */
    public static boolean isHostValid(String host){
        String ipRegex= "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String urlRegex = "^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return host.matches(ipRegex) || host.matches(urlRegex);
    }

    /**
     * This method prints an error message and closes the program
     */
    private static void exitError() {
        Logger.println(InterfaceMessages.CLIENT_USAGE_STRING);
        System.exit(Constants.EXIT_ERROR);
    }


    /**
     * This method starts a client
     * @param args the parameters from command line
     */
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
                    String host = options.valueOf(CLIArguments.HOST).toString().trim();
                    if (!isHostValid(host)) {
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
                    ClientGUI.setClient(client);
                    ClientGUI.setDebug(debug);
                    Application.launch(ClientGUI.class);
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
