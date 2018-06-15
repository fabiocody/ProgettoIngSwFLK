package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.util.*;
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

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonParser jsonParser;
    private Queue<JsonObject> responseBuffer;
    private final Object responseBufferLock = new Object();
    private Thread recvThread;
    private boolean toBeKilled = false;

    // FLAGS

    /**
     * This is the constructor of the client
     *
     * @param host the IP address of the server you want to connect to
     * @param port the port of the server to which it is listening
     * @param debug debug messages will be shown if true
     */
    SocketClient(String host, int port, boolean debug) {
        super(host, port, debug);
        this.jsonParser = new JsonParser();
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
        this.toBeKilled = true;
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
        Logger.debug("pollResponsesBuffer called");
        synchronized (responseBufferLock) {
            while (responseBuffer.peek() == null) {
                try {
                    Logger.debug("Waiting on pollResponseBuffer");
                    responseBufferLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        boolean run = true;
        while (run) {
            JsonObject inputJson = null;
            try {
                inputJson = this.jsonParser.parse(this.readLine()).getAsJsonObject();
                Logger.debug(inputJson.toString());
            } catch (IOException | NullPointerException e) {
                Logger.connectionLost();
                System.exit(Constants.INDEX_CONSTANT);
            }
            Methods recvMethod;
            try {
                recvMethod = Methods.getAsMethods(inputJson.get(JsonFields.METHOD).getAsString());
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
                    this.updateWaitingPlayers(inputJson);
                    break;
                case WR_TIMER_TICK:
                    this.wrTimerTick(inputJson);
                    break;
                case GAME_SETUP:
                    this.gameSetup(inputJson);
                    break;
                case TURN_MANAGEMENT:
                    this.turnManagement(inputJson);
                    break;
                case PLAYERS:
                    this.inGamePlayers(inputJson);
                    break;
                case PUBLIC_OBJECTIVE_CARDS:
                    this.publicObjectiveCards(inputJson);
                    break;
                case TOOL_CARDS:
                    this.updateToolCards(inputJson);
                    break;
                case WINDOW_PATTERNS:
                    this.updateWindowPatterns(inputJson);
                    break;
                case DRAFT_POOL:
                    this.updateDraftPool(inputJson);
                    break;
                case PROBE:
                    this.probe();
                    break;
                case ROUND_TRACK_DICE:
                    this.updateRoundTrack(inputJson);
                    break;
                case FAVOR_TOKENS:
                    this.updateFavorTokens(inputJson);
                    break;
                case FINAL_SCORES:
                    this.updateFinalScores(inputJson);
                    break;
                case GAME_TIMER_TICK:
                    this.gameTimerTick(inputJson);
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
            Logger.connectionLost();
            System.exit(Constants.INDEX_CONSTANT);
        }
        return line;
    }

    private void probe() {
        this.sendMessage(new JsonObject(), Methods.PROBE.getString());
        this.rescheduleProbeTimer();
    }

    private void sendMessage(JsonObject payload, String method) {
        if (this.getUuid() != null)
            payload.addProperty(JsonFields.PLAYER_ID, this.getUuid().toString());
        payload.addProperty(JsonFields.METHOD, method);
        Logger.debug("PAYLOAD " + payload);
        out.println(payload.toString());
    }

    /**
     * This method handles log in of a player
     */
    @Override
    UUID addPlayer(String nickname) {
        this.setNickname(nickname);
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.NICKNAME, nickname);
        Logger.debug("PAYLOAD " + payload.toString());
        this.sendMessage(payload, Methods.ADD_PLAYER.getString());
        JsonObject input = this.pollResponseBuffer();
        if (input.get(JsonFields.LOGGED).getAsBoolean()) {
            this.setUuid(UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString()));
            Logger.debug("INPUT " + input);
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
            return getUuid();
        } else {
            return null;
        }
    }

    /**
     * This method is used when a player chooses his window pattern at the beginning of the game
     *
     * @param patternIndex the index of the chosen window pattern
     */
    @Override
    void choosePattern(int patternIndex) {
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.PATTERN_INDEX, patternIndex);
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload, Methods.CHOOSE_PATTERN.getString());
    }

    /**
     * This method is used to place a die from the draft pool in the specified position
     *
     * @param draftPoolIndex the index of the draft pool which contains the die
     * @param x the column index in which the user wants to place the die
     * @param y the row index in which the user wants to place the die
     * @return boolean true if the die place was successful, false otherwise
     */
    @Override
    boolean placeDie(int draftPoolIndex, int x, int y){
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.DRAFT_POOL_INDEX, draftPoolIndex);
        arg.addProperty(JsonFields.TO_CELL_X, x);
        arg.addProperty(JsonFields.TO_CELL_Y, y);
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload,Methods.PLACE_DIE.getString());
        JsonObject input = this.pollResponseBuffer();
        Logger.debug("INPUT " + input);
        return input.get(JsonFields.RESULT).getAsBoolean();
    }

    /**
     * This method handles the request from a user to use a tool card
     *
     * @param cardIndex index of the specified tool card
     * @param data JsonObject containing all the necessary fields filled with information given by the user
     * @return
     */
    @Override
    boolean useToolCard(int cardIndex, JsonObject data){
        JsonObject payload = new JsonObject();
        JsonObject arg = new JsonObject();
        arg.addProperty(JsonFields.CARD_INDEX, cardIndex);
        arg.add(JsonFields.DATA, data); //different data for each tool card
        payload.add(JsonFields.ARG, arg);
        this.sendMessage(payload,Methods.USE_TOOL_CARD.getString());
        JsonObject input = this.pollResponseBuffer();
        Logger.debug("INPUT " + input);
        return input.get(JsonFields.RESULT).getAsBoolean();
    }

    /**
     * This method is used to request the information needed to use the specified tool card
     *
     * @param cardIndex the index of the tool card that the user wants to use
     * @return JsonObject containing the required fields for the specified tool card
     */
    @Override
    JsonObject requiredData(int cardIndex){
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.CARD_INDEX, cardIndex);
        this.sendMessage(payload, Methods.REQUIRED_DATA.getString());
        JsonObject input = this.pollResponseBuffer();
        Logger.debug("INPUT " + input);
        return input;
    }

    /**
     * This method is used to pass to the next turn
     */
    @Override
    void nextTurn() {
        JsonObject payload = new JsonObject();
        this.sendMessage(payload, Methods.NEXT_TURN.getString());
    }

    /**
     * This method is used to print out an updated list of waiting players
     *
     * @param input data from the server
     */
    private void updateWaitingPlayers(JsonObject input) {
        this.setChanged();
        this.notifyObservers(input.get(JsonFields.PLAYERS).getAsJsonArray());
    }

    /**
     * This method is used to print out an updated timer
     *
     * @param input data from the server
     */
    private void wrTimerTick(JsonObject input) {
        String tick = String.valueOf(input.get(JsonFields.TICK).getAsString());
        this.setChanged();
        this.notifyObservers(Arrays.asList(NotificationsMessages.WR_TIMER_TICK, tick));
    }

    private void gameTimerTick(JsonObject input) {
        String tick = String.valueOf(input.get(JsonFields.TICK).getAsInt());
        this.setChanged();
        this.notifyObservers(Arrays.asList(NotificationsMessages.GAME_TIMER_TICK, tick));
    }

    /**
     * This method is used to set up the game for a player providing the private objective card and the window patterns
     * from which the will have to choose one
     *
     * @param input JsonObject received from the server containing the data for the private objective card and the 4 window patterns
     */
    private void gameSetup(JsonObject input) {
        String privateObjectiveCardString = NotificationsMessages.PRIVATE_OBJECTIVE_CARD + "Il tuo obiettivo privato è:\n";
        privateObjectiveCardString += input.get(JsonFields.PRIVATE_OBJECTIVE_CARD).getAsJsonObject().get(JsonFields.NAME).getAsString();
        privateObjectiveCardString += " - ";
        privateObjectiveCardString += input.get(JsonFields.PRIVATE_OBJECTIVE_CARD).getAsJsonObject().get(JsonFields.DESCRIPTION).getAsString();
        this.setChanged();
        this.notifyObservers(privateObjectiveCardString);
        JsonArray windowPatterns = input.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        List<String> selectableWPStrings = StreamSupport.stream(windowPatterns.spliterator(), false)
                .map(obj -> obj.getAsJsonObject().get(JsonFields.CLI_STRING).getAsString())
                .collect(Collectors.toList());
        selectableWPStrings.add(0, NotificationsMessages.SELECTABLE_WINDOW_PATTERNS);
        this.setChanged();
        this.notifyObservers(selectableWPStrings);
    }

    private void inGamePlayers(JsonObject input) {
        this.setChanged();
        this.notifyObservers(input.get(JsonFields.PLAYERS).getAsJsonArray());
    }

    /**
     * This method is used to update the tool cards seen by the client
     *
     * @param input JsonObject containing the data of the tool cards
     */
    private void updateToolCards(JsonObject input){
        JsonArray toolCards = input.getAsJsonArray(JsonFields.TOOL_CARDS);
        List<String> toolCardsStrings = new ArrayList<>();
        for(JsonElement obj : toolCards){
            String toolCardString = NotificationsMessages.TOOL_CARDS;
            toolCardString += obj.getAsJsonObject().get(JsonFields.NAME).getAsString();
            toolCardString += " - ";
            toolCardString += obj.getAsJsonObject().get(JsonFields.DESCRIPTION).getAsString();
            //TODO toolCardString += obj.getAsJsonObject().get("used").getAsString();
            toolCardsStrings.add(toolCardString);
        }
        this.setChanged();
        this.notifyObservers(toolCardsStrings);
    }

    /**
     * This method is used to update the window patterns seen by the client
     *
     * @param input JsonObject containing the data of the window patterns
     */
    private void updateWindowPatterns(JsonObject input){
        List<String> windowPatternsList = new ArrayList<>();
        windowPatternsList.add(NotificationsMessages.UPDATE_WINDOW_PATTERNS);
        Set<Map.Entry<String, JsonElement>> entrySet = input.get(JsonFields.WINDOW_PATTERNS).getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String keyValue = entry.getKey() + "$" + entry.getValue().getAsJsonObject().get(JsonFields.CLI_STRING).getAsString();
            windowPatternsList.add(keyValue);
        }
        this.setChanged();
        this.notifyObservers(windowPatternsList);
    }

    /**
     * This method is used to show the bublic objective cards to the client
     *
     * @param input
     */
    private void publicObjectiveCards(JsonObject input){
        JsonArray publicObjectiveCards= input.getAsJsonArray(JsonFields.PUBLIC_OBJECTIVE_CARDS);
        List<String> publicObjectiveCardsStrings = new ArrayList<>();
        for(JsonElement obj : publicObjectiveCards){
            String publicObjectiveCardString = NotificationsMessages.PUBLIC_OBJECTIVE_CARDS;
            publicObjectiveCardString += obj.getAsJsonObject().get(JsonFields.NAME).getAsString();
            publicObjectiveCardString += " $- ";
            publicObjectiveCardString += obj.getAsJsonObject().get(JsonFields.DESCRIPTION).getAsString();
            publicObjectiveCardString += " $$- ";
            if(obj.getAsJsonObject().get(JsonFields.VICTORY_POINTS).isJsonNull())
                publicObjectiveCardString += "#";
            else
                publicObjectiveCardString += obj.getAsJsonObject().get(JsonFields.VICTORY_POINTS).getAsInt();
            publicObjectiveCardsStrings.add(publicObjectiveCardString);
        }
        this.setChanged();
        this.notifyObservers(publicObjectiveCardsStrings);
    }

    /**
     * This method is used to update the draft pool seen by the client
     *
     * @param input JsonObject containing the data of the draft pool
     */
    private void updateDraftPool(JsonObject input){
        JsonArray draftPoolDice = input.getAsJsonArray(JsonFields.DICE);
        List<String> draftPoolDieStrings = new ArrayList<>();
        for(JsonElement obj : draftPoolDice){
            String dieString = NotificationsMessages.DRAFT_POOL;
            dieString += obj.getAsJsonObject().get(JsonFields.CLI_STRING).getAsString();
            draftPoolDieStrings.add(dieString);
        }
        this.setChanged();
        this.notifyObservers(draftPoolDieStrings);
    }

    private void updateRoundTrack(JsonObject input){
        this.setChanged();
        this.notifyObservers(input.get(JsonFields.CLI_STRING).getAsString());
    }

    private void turnManagement(JsonObject input) {
        List<String> turnManagamentStrings = new ArrayList<>();
        turnManagamentStrings.add(NotificationsMessages.TURN_MANAGEMENT);
        turnManagamentStrings.add(input.get(JsonFields.CURRENT_ROUND).getAsString());
        turnManagamentStrings.add(input.get(JsonFields.GAME_OVER).getAsString());
        turnManagamentStrings.add(input.get(JsonFields.ACTIVE_PLAYER).getAsString());
        String suspendedPlayers = input.get(JsonFields.SUSPENDED_PLAYERS).getAsJsonArray().toString();
        suspendedPlayers = suspendedPlayers
                .substring(1, suspendedPlayers.length() - 1)
                .replace("\",", "$")
                .replace("\"", "");
        turnManagamentStrings.add(suspendedPlayers);
        this.setChanged();
        this.notifyObservers(turnManagamentStrings);
    }

    private void updateFavorTokens(JsonObject input){
        this.setChanged();
        this.notifyObservers(input);
    }

    private void updateFinalScores(JsonObject input){
        this.setChanged();
        this.notifyObservers(input);
    }

}