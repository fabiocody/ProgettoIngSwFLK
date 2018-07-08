package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.shared.util.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * This class contains the methods used by a client who joined via socket
 * @author Team
 */
public class SocketClient extends ClientNetwork {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Queue<JsonObject> responseBuffer;
    private final Object responseBufferLock = new Object();
    private Thread recvThread;
    private boolean recvRun = true;

    /**
     * This is the constructor of the client
     *
     * @param host the IP address of the server you want to connect to
     * @param port the port of the server to which it is listening
     * @param debug debug messages will be shown if true
     */
    public SocketClient(String host, int port, boolean debug) {
        super(host, port, debug);
        this.responseBuffer = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void setup() throws IOException {
        this.socket = new Socket(getHost(), getPort());
        this.out = new PrintWriter(socket.getOutputStream(), true);
        recvThread = new Thread(this::recv);
        recvThread.setDaemon(true);
        recvThread.start();
    }

    @Override
    public void teardown() throws IOException {
        recvRun = false;
        if (recvThread != null) recvThread.interrupt();
        if (this.out != null) this.out.close();
        if (this.socket != null) this.socket.close();
    }

    /**
     * This method waits for responses from the server
     *
     * @return a JsonObject containing the responses
     */
    private JsonObject pollResponseBuffer() {
        Logger.debug("pollResponsesBuffer called");
        synchronized (responseBufferLock) {
            while (responseBuffer.peek() == null) {
                try {
                    Logger.debug("Waiting on pollResponseBuffer");
                    responseBufferLock.wait();
                } catch (InterruptedException e) {
                    Logger.printStackTraceConditionally(e);
                    Thread.currentThread().interrupt();
                }
            }
            Logger.debug("Returning from pollResponsesBuffer");
            return responseBuffer.poll();
        }
    }

    /**
     * This method, which is executed on a separate thread, waits for the client to execute a valid method
     */
    private void recv() {
        while (recvRun) {
            JsonObject inputJson;
            try {
                inputJson = getJsonParser().parse(this.readLine()).getAsJsonObject();
                Logger.debug(inputJson.toString());
            } catch (IOException | NullPointerException e) {
                if (recvRun) connectionError(e);
                return;
            }
            Methods recvMethod;
            try {
                recvMethod = Methods.fromString(inputJson.get(JsonFields.METHOD).getAsString());
            } catch (NoSuchElementException e) {
                Logger.error("METHOD NOT RECOGNIZED");
                continue;
            }
            Logger.debug("Received method " + recvMethod.getString());
            switch (recvMethod) {
                case ADD_PLAYER:
                case CHOOSE_PATTERN:
                case NEXT_TURN:
                case PLACE_DIE:
                case USE_TOOL_CARD:
                case REQUIRED_DATA:
                    synchronized (responseBufferLock) {
                        responseBuffer.add(inputJson);
                        responseBufferLock.notifyAll();
                        Logger.debug("Added " + inputJson + " to responsesBuffer");
                    }
                    break;
                case UPDATE_WAITING_PLAYERS:
                case WR_TIMER_TICK:
                case GAME_TIMER_TICK:
                case GAME_SETUP:
                case TURN_MANAGEMENT:
                case PLAYERS:
                case PUBLIC_OBJECTIVE_CARDS:
                case DRAFT_POOL:
                case ROUND_TRACK:
                case FAVOR_TOKENS:
                case TOOL_CARDS:
                case WINDOW_PATTERNS:
                    this.setChanged();
                    this.notifyObservers(inputJson);
                    break;
                case FINAL_SCORES:
                    gameEnding = true;
                    setChanged();
                    notifyObservers(inputJson);
                    break;
                case CANCEL_TOOL_CARD_USAGE:
                    break;
                case PROBE:
                    this.probe();
                    break;
            }
        }
    }

    /**
     * This method analyzes the string of an incoming message
     *
     * @return the received string
     * @throws IOException socket error
     */
    private String readLine() throws IOException {
        if (this.in == null)
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = in.readLine();
        if (line == null) {
            connectionError("readLine");
        }
        return line;
    }

    /**
     * This method sends probe messages
     */
    private void probe() {
        this.sendMessage(new JsonObject(), Methods.PROBE);
        this.rescheduleProbeTimer();
    }


    /**
     * This method sends a Json message to the server
     * @param payload the payload of the message to send
     * @param method the name of the method that sends the message
     */
    private void sendMessage(JsonObject payload, Methods method) {
        if (getUUID() != null)
            payload.addProperty(JsonFields.PLAYER_ID, getUUID().toString());
        payload.addProperty(JsonFields.METHOD, method.getString());
        Logger.debugPayload(payload);
        out.println(payload.toString());
    }

    @Override
    public UUID addPlayer(String nickname) {
        setNickname(nickname);
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.NICKNAME, nickname);
        Logger.debugPayload(payload);
        this.sendMessage(payload, Methods.ADD_PLAYER);
        JsonObject input = this.pollResponseBuffer();
        if (input.get(JsonFields.LOGGED).getAsBoolean()) {
            setUUID(UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString()));
            Logger.debugInput(input);
            if (input.get(JsonFields.RECONNECTED).getAsBoolean()) {
                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setChanged();
                        notifyObservers(input);
                    }
                }, 100);
            } else {
                this.rescheduleProbeTimer();
            }
            return getUUID();
        } else {
            return null;
        }
    }

    @Override
    public boolean choosePattern(int patternIndex) {
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.PATTERN_INDEX, patternIndex);
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload, Methods.CHOOSE_PATTERN);
        JsonObject input = pollResponseBuffer();
        return input.get(JsonFields.RESULT).getAsBoolean();
    }

    @Override
    public JsonObject placeDie(int draftPoolIndex, int x, int y){
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.DRAFT_POOL_INDEX, draftPoolIndex);
        arg.addProperty(JsonFields.TO_CELL_X, x);
        arg.addProperty(JsonFields.TO_CELL_Y, y);
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload,Methods.PLACE_DIE);
        JsonObject input = this.pollResponseBuffer();
        Logger.debugInput(input);
        return input;
    }

    @Override
    public JsonObject requiredData(int cardIndex){
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.CARD_INDEX, cardIndex);
        this.sendMessage(payload, Methods.REQUIRED_DATA);
        JsonObject input = this.pollResponseBuffer();
        Logger.debugInput(input);
        return input;
    }

    @Override
    public JsonObject useToolCard(int cardIndex, JsonObject data){
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.CARD_INDEX, cardIndex);
        arg.add(JsonFields.DATA, data); //different data for each tool card
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload,Methods.USE_TOOL_CARD);
        JsonObject input = this.pollResponseBuffer();
        Logger.debugInput(input);
        return input;
    }

    @Override
    public void cancelToolCardUsage(int cardIndex) {
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.CARD_INDEX, cardIndex);
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload, Methods.CANCEL_TOOL_CARD_USAGE);
    }

    @Override
    public void nextTurn() {
        JsonObject payload = new JsonObject();
        this.sendMessage(payload, Methods.NEXT_TURN);
    }

}