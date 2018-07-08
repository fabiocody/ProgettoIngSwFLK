package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.shared.util.*;
import java.io.IOException;
import java.util.*;

/**
 * This is the base class for <code>SocketClient</code> and <code>RMIClient</code>
 * @author Team
 */
public abstract class ClientNetwork extends Observable {

    private static ClientNetwork instance;

    private String host;
    private int port;

    private String nickname;
    private UUID uuid;

    private JsonParser jsonParser;
    private Timer probeTimer;
    boolean gameEnding = false;

    /**
     * This is the constructor of the client
     * @param host the IP or URL address of the server you want to connect to
     * @param port the port of the server to which it is listening
     * @param debug debug messages will be shown if true
     */
    ClientNetwork(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        Logger.setDebug(debug);
    }

    /**
     * @return this user-specific <code>clientNetwork</code> instance
     */
    public static ClientNetwork getInstance() {
        return instance;
    }

    /**
     * @param network the <code>clientNetwork</code> instance to set as user-specific
     */
    public static void setInstance(ClientNetwork network) {
        instance = network;
    }

    /**
     * @return the IP or URL address of this server
     */
    String getHost() {
        return host;
    }

    /**
     * @return the port of this server
     */
    int getPort() {
        return port;
    }

    /**
     * @return the nickname of the client related to this client
     */
    String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname of the client related to this client
     */
    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the UUID of the client related to this client
     */
    UUID getUUID() {
        return uuid;
    }

    /**
     * @param uuid the UUID of the client related to this client
     */
    void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the JsonParser of the class
     */
    JsonParser getJsonParser() {
        if (jsonParser == null)
            jsonParser = new JsonParser();
        return jsonParser;
    }

    /**
     * This method sets up the client network
     * @throws IOException thrown if a IO error occurs
     */
    public abstract void setup() throws IOException;


    /**
     * This method closes the client network
     * @throws IOException thrown if a IO error occurs
     */
    public abstract void teardown() throws IOException;

    /**
     * This method handles log in of a player
     */
    public abstract UUID addPlayer(String nickname);

    /**
     * This method is used when a player chooses his window pattern at the beginning of the game
     *
     * @param patternIndex the index of the chosen window pattern
     */
    public abstract boolean choosePattern(int patternIndex);

    /**
     * This method is used to place a die from the draft pool in the specified position
     *
     * @param draftPoolIndex the index of the draft pool which contains the die
     * @param x the column index in which the user wants to place the die
     * @param y the row index in which the user wants to place the die
     * @return input the result message of the placement
     */
    public abstract JsonObject placeDie(int draftPoolIndex, int x, int y);

    /**
     * This method is used to request the information needed to use the specified tool card
     *
     * @param cardIndex the index of the tool card that the user wants to use
     * @return JsonObject containing the required fields for the specified tool card
     */
    public abstract JsonObject requiredData(int cardIndex);

    /**
     * This method handles the request from a user to use a tool card
     *
     * @param cardIndex index of the specified tool card
     * @param data JsonObject containing all the necessary fields filled with information given by the user
     * @return JsonObject containing the result of the usage
     */
    public abstract JsonObject useToolCard(int cardIndex, JsonObject data);

    /**
     * This methods handles the cancel action while using a tool card
     * @param cardIndex the index of the chosen card
     */
    public abstract void cancelToolCardUsage(int cardIndex);

    /**
     * This method is used to pass to the next turn
     */
    public abstract void nextTurn();

    /**
     * This method handles the rescheduling of the timer
     */
    void rescheduleProbeTimer() {
        if (this.probeTimer != null) {
            this.probeTimer.cancel();
            this.probeTimer.purge();
        }
        this.probeTimer = new Timer(true);
        this.probeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gameEnding)
                    connectionError("rescheduleProbeTimer");
            }
        }, Constants.PROBE_TIMEOUT * 2000);
    }

    /**
     * This method handles a connection error, showing it to the user and notifying the observers
     * @param message the message to show the user
     * @param e the exception thrown (required for debugging reasons)
     */
    private void connectionError(String message, Throwable e) {
        Logger.connectionLost();
        if (message != null) Logger.conditionalError(message);
        Logger.printStackTraceConditionally(e);
        JsonObject obj = new JsonObject();
        obj.addProperty(JsonFields.EXIT_ERROR, true);
        setChanged();
        notifyObservers(obj);
    }

    /**
     * This method handles a connection error, showing it to the user and notifying the observers
     * but without considering exceptions
     * @param message the message to show the user
     */
    void connectionError(String message) {
        connectionError(message, null);
    }

    /**
     * This method handles a connection error, showing it to the user and notifying the observers
     * but without printing any messages
     * @param e the exception thrown (required for debugging reasons)
     */
    void connectionError(Throwable e) {
        connectionError(null, e);
    }

}
