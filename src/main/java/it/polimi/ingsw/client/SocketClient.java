package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.server.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.*;


/**
 * This is the main client class
 *
 * @author Team
 */
public class SocketClient extends ClientNetwork {

    private String ip;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonParser jsonParser;
    private Queue<JsonObject> responseBuffer;
    private final Object responseBufferLock = new Object();
    private Thread recvThread;
    private Timer probeTimer;

    private String nickname;
    private UUID uuid;

    // FLAGS
    private boolean debugActive;

    /**
     * This is the constructor of the client
     *
     * @param ip the IP address of the server you want to connect to
     * @param port the port of the server to which it is listening
     * @param debug debug messages will be shown if true
     */
    SocketClient(String ip, int port, boolean debug) {
        this.ip = ip;
        this.port = port;
        this.debugActive = debug;
        this.jsonParser = new JsonParser();
        this.responseBuffer = new ConcurrentLinkedQueue<>();
    }

    public void setup() throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        recvThread = new Thread(this::recv);
        recvThread.setDaemon(true);
        recvThread.start();
    }

    public void teardown() throws IOException {
        recvThread.interrupt();
        if (this.in != null) this.in.close();
        if (this.out != null) this.out.close();
        if (this.socket != null) this.socket.close();
    }

    /**
     * This method waits for responses from the server
     *
     * @return a JsonObject containing the responses
     */
    private JsonObject pollResponseBuffer() {
        debug("pollResponsesBuffer called");
        synchronized (responseBufferLock) {
            while (responseBuffer.peek() == null) {
                try {
                    debug("Waiting on pollResponseBuffer");
                    responseBufferLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            debug("Returning from pollResponsesBuffer");
            return responseBuffer.poll();
        }
    }

    /**
     * This method is used to print standard messages
     *
     * @param message that we want to print
     */
    private void log(String message) {
        System.out.println(message);
    }

    /**
     * This method is used to print messages intended for debugging
     *
     * @param message that we want to print out
     */
    private void debug(String message) {
        if (this.debugActive)
            System.out.println("[DEBUG] " + message);
    }

    /**
     * This method is used to print error messages
     *
     * @param message that we want to print out
     */
    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    /**
     * This method, which is executed on a separate thread, waits for the client to execute a valid method
     */
    private void recv() {
        boolean run = true;
        while (run) {
            JsonObject inputJson;
            try {
                inputJson = this.jsonParser.parse(this.readLine()).getAsJsonObject();
                debug(inputJson.toString());
            } catch (IOException e) {
                error("Connection aborted");
                break;
            }
            Methods recvMethod;
            try {
                recvMethod = Methods.getAsMethods(inputJson.get("method").getAsString());
            } catch (NoSuchElementException e) {
                error("METHOD NOT RECOGNIZED");
                continue;
            }
            debug("Received method " + recvMethod.getString());
            switch (recvMethod) {
                case ADD_PLAYER:
                case SUBSCRIBE_TO_WR_TIMER:
                case SUBSCRIBE_TO_GAME_TIMER:
                case CHOOSE_PATTERN:
                case NEXT_TURN:
                case PLACE_DIE:
                case USE_TOOL_CARD:
                    synchronized (responseBufferLock) {
                        responseBuffer.add(inputJson);
                        responseBufferLock.notifyAll();
                        debug("Added " + inputJson + " to responsesBuffer");
                    }
                    break;
                case UPDATE_WAITING_PLAYERS:
                    this.updateWaitingPlayers(inputJson);
                    break;
                case WR_TIMER_TICK:
                    this.wrTimerTick(inputJson);
                    break;
                case GAME_SETUP:
                    this.gameSetup(inputJson);
                    break;
                case GAME_STARTED:
                    this.gameStarted(inputJson);
                    break;
                case GAME_TIMER_TICK:
                case PLAYERS:
                case FINAL_SCORES:
                case PUBLIC_OBJECTIVE_CARDS:
                case TOOL_CARDS:
                case FAVOR_TOKENS:
                case WINDOW_PATTERNS:
                case ROUND_TRACK_DICE:
                case DRAFT_POOL:
                    log(inputJson.toString());
                    break;
                case PROBE:
                    this.probe();
                    break;
            }
        }
    }

    /**
     * this method analyzes the string of an incoming message
     *
     * @return the received string
     * @throws IOException socket error
     */
    private String readLine() throws IOException {
        if (this.in == null)
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = in.readLine();
        if (line == null) {
            error("DISCONNECTED");
        }
        return line;
    }

    private void rescheduleProbeTimer() {
        if (this.probeTimer != null) {
            this.probeTimer.cancel();
            this.probeTimer.purge();
        }
        this.probeTimer = new Timer(true);
        this.probeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                error("Connection lost");
                System.exit(42);
            }
        }, 10 * 1000);
    }

    private void probe() {
        this.sendMessage(new JsonObject(), "probe");
        this.rescheduleProbeTimer();
    }

    private void sendMessage(JsonObject payload, String method) {
        if (this.uuid != null)
            payload.addProperty("playerID", this.uuid.toString());
        payload.addProperty("method", method);
        debug("PAYLOAD " + payload);
        out.println(payload.toString());
    }

    /**
     * This method handles log in of a player
     */
    public UUID addPlayer(String nickname) {
        this.nickname = nickname;
        JsonObject payload = new JsonObject();
        payload.addProperty("nickname", nickname);
        debug("PAYLOAD " + payload.toString());
        this.sendMessage(payload, "addPlayer");
        JsonObject input = this.pollResponseBuffer();
        if (input.get("logged").getAsBoolean()) {
            uuid = UUID.fromString(input.get("UUID").getAsString());
            debug("INPUT " + input);
            JsonArray players = input.get("players").getAsJsonArray();
            debug("SIZE: " + players.size());
            if (players.size() < 4) {
                new Timer(true)
                        .schedule(new TimerTask() {
                            @Override
                            public void run() {
                                updateWaitingPlayers(input);
                            }
                        }, 100);
                this.subscribeToWRTimer();
            }
            this.rescheduleProbeTimer();
            return uuid;
        } else {
            return null;
        }
    }

    /**
     * This method is used to subscribe a client to the waiting room timer, so that the client will receive regular
     * updates
     */
    private void subscribeToWRTimer() {
        JsonObject payload = new JsonObject();
        this.sendMessage(payload, "subscribeToWRTimer");
        debug("INPUT " + this.pollResponseBuffer());
    }

    void choosePattern(int patternIndex) {
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty("patternIndex", patternIndex);
        payload.add("arg", arg);
        this.sendMessage(payload, "choosePattern");
    }

    /**
     * This method is used to print out an updated list of waiting players
     *
     * @param input data from the server
     */
    private void updateWaitingPlayers(JsonObject input) {
        setChanged();
        notifyObservers(input.get("players").getAsJsonArray());
    }

    /**
     * This method is used to print out an updated timer
     *
     * @param input data from the server
     */
    private void wrTimerTick(JsonObject input) {
        setChanged();
        notifyObservers(input.get("tick").getAsInt());
    }

    private void gameSetup(JsonObject input) {
        // TODO Private Objectve Card
        String privateObjectiveCardString = "PrivateObjectiveCard$Il tuo obiettivo privato è:\n";
        privateObjectiveCardString += input.get("privateObjectiveCard").getAsJsonObject().get("name").getAsString();
        privateObjectiveCardString += " - ";
        privateObjectiveCardString += input.get("privateObjectiveCard").getAsJsonObject().get("description").getAsString();
        setChanged();
        notifyObservers(privateObjectiveCardString);
        JsonArray windowPatterns = input.getAsJsonArray("windowPatterns");
        List strings = StreamSupport.stream(windowPatterns.spliterator(), false)
                .map(obj -> obj.getAsJsonObject().get("cliString").getAsString())
                .collect(Collectors.toList());
        setChanged();
        notifyObservers(strings);
    }

    private void gameStarted(JsonObject input) {
        this.setChanged();
        this.notifyObservers(input.get("activePlayer").getAsString().equals(nickname));
    }

}