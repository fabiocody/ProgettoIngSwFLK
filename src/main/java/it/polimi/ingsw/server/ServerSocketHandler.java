package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.*;
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {
    // Observes CountdownTimer (from WaitingRoom and TurnManager), WaitingRoom and Game

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private WaitingRoomEndPoint waitingRoomEndPoint;
    private GameEndPoint gameEndPoint;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;
    private Game game;
    private boolean run = true;
    private Thread probeThread;
    private boolean probed = true;

    public ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
        /*try {
            this.clientSocket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }*/
        this.jsonParser = new JsonParser();
        this.waitingRoomEndPoint = new WaitingRoomEndPoint();
        waitingRoomEndPoint.subscribeToWaitingRoom(this);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void debug(String message) {
        if (SagradaServer.getInstance().isDebugActive())
            System.out.println("[DEBUG] " + message);
    }

    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    private void probe() {
        while (Constants.INDEX_CONSTANT == Constants.INDEX_CONSTANT) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                break;
            }
            if (!probed) {
                error("Probe error");
                notifyDisconnectedUser();
                Thread.currentThread().interrupt();
                waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
                waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
            }
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.PROBE.getString());
            out.println(payload.toString());
            probed = false;
        }
    }

    private void notifyDisconnectedUser() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        log("Disconnected " + address + " (nickname: " + nickname + ")");
        waitingRoomEndPoint.removePlayer(nickname);
        //game.getTurnManager().suspendPlayer(nickname);
        run = false;
        Thread.currentThread().interrupt();
    }

    // REQUESTS HANDLER

    @Override
    public void run() {
        try (Socket socket = this.clientSocket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true)) {
            log("Connected to " + socket.getInetAddress() + ":" + socket.getPort() + " via socket");
            while (this.run) {
                JsonObject input;
                try {
                    input = this.parseJson(this.readLine());
                } catch (NullPointerException e) {
                    debug("JSON parsing failed");
                    continue;
                }
                debug("Received: " + input.toString());
                // UUID validation
                if (!input.get(JsonFields.METHOD).getAsString().equals(Methods.ADD_PLAYER.getString()) &&
                        !input.get(JsonFields.PLAYER_ID).getAsString().equals(this.uuid.toString())) {
                    error("AUTH ERROR");
                    continue;
                }
                Methods calledMethod;
                try {
                    calledMethod = Methods.getAsMethods(input.get(JsonFields.METHOD).getAsString());
                } catch (NoSuchElementException e) {
                    error("METHOD NOT RECOGNIZED");
                    continue;
                }
                debug("Received method " + calledMethod.getString());
                switch (calledMethod) {
                    case ADD_PLAYER:
                        this.addPlayer(input);
                        break;
                    case SUBSCRIBE_TO_WR_TIMER:
                        this.subscribeToWRTimer();
                        break;
                    case SUBSCRIBE_TO_GAME_TIMER:
                        // TODO
                        break;
                    case CHOOSE_PATTERN:
                        this.choosePattern(input);
                        break;
                    case NEXT_TURN:
                        this.nextTurn();
                        break;
                    case PLACE_DIE:
                        this.placeDie(input);
                        break;
                    case USE_TOOL_CARD:
                        this.useToolCard(input);
                        break;
                    case REQUIRED_DATA_FOR_TOOL_CARD:
                        this.requiredData(input);
                        break;
                    case PROBE:
                        probed = true;
                        break;
                    default:
                        error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (Exception e) {
            error("run");
            e.printStackTrace();
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.notifyDisconnectedUser();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
            waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
            waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
        }
        return line;
    }

    private JsonObject parseJson(String string) {
        debug("Parsing JSON");
        return this.jsonParser.parse(string).getAsJsonObject();
    }

    private void addPlayer(JsonObject input) {
        debug("addPlayer called");
        debug("INPUT " + input.toString());
        String tempNickname = input.get(JsonFields.NICKNAME).getAsString();
        try {
            uuid = this.waitingRoomEndPoint.addPlayer(tempNickname);
            nickname = tempNickname;
            log(nickname + " logged in (" + uuid + ")");
            debug("UUID: " + uuid.toString());
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.PLAYER_ID, uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (Player p : this.waitingRoomEndPoint.getWaitingPlayers())
                waitingPlayers.add(p.getNickname());
            payload.add(JsonFields.PLAYERS, waitingPlayers);
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
            this.probeThread = new Thread(this::probe);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            log("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, false);
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        }
    }

    private void subscribeToWRTimer() {
        this.waitingRoomEndPoint.subscribeToWaitingRoomTimer(this);
        debug("Timer registered");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.SUBSCRIBE_TO_WR_TIMER.getString());
        payload.addProperty(JsonFields.RESULT, true);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void choosePattern(JsonObject input) {
        int patternIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.PATTERN_INDEX).getAsInt();
        this.gameEndPoint.choosePattern(uuid, patternIndex);
        log(nickname + " has chosen pattern " + patternIndex);
    }

    private void placeDie(JsonObject input){
        int draftPoolIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int x = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_X).getAsInt();
        int y = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_Y).getAsInt();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD,Methods.PLACE_DIE.getString());
        try{
            this.gameEndPoint.placeDie(uuid, draftPoolIndex, x, y);
            payload.addProperty(JsonFields.RESULT, true);
            log(nickname + " placed a die");
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (InvalidPlacementException | DieAlreadyPlacedException e){
            payload.addProperty(JsonFields.RESULT, false);
            log(nickname + " die placement was refused");
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        }
    }

    private void useToolCard(JsonObject input) {
        int cardIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.CARD_INDEX).getAsInt();
        JsonObject data = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DATA).getAsJsonObject();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        data.addProperty(JsonFields.PLAYER_ID, id.toString()); //serve nickname, non UUID
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD,JsonFields.USE_TOOL_CARD);
        try {
            this.gameEndPoint.useToolCard(id, cardIndex, data);
            payload.addProperty(JsonFields.RESULT, true);
            log(nickname + " used a tool card");
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (RemoteException | InvalidEffectArgumentException | InvalidEffectResultException e) {
            payload.addProperty(JsonFields.RESULT, false);
            log(nickname + " usage of tool card was refused");
            debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        }
    }

    private void requiredData(JsonObject input){
        int cardIndex = input.get(JsonFields.CARD_INDEX).getAsInt();
        JsonObject payload = this.gameEndPoint.requiredData(cardIndex);
        if (payload.get("data").getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX) && this.game.getRoundTrack().getAllDice().isEmpty()){
            payload.get("data").getAsJsonObject().addProperty("impossibleToUseToolCard", JsonFields.ROUND_TRACK);
        }
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void nextTurn() {
        this.gameEndPoint.nextTurn();
        log(nickname + " has ended his turn");
    }

    private void updatePlayersList() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLAYERS.getString());
        JsonArray playersJSON = new JsonArray();
        for (String name : this.gameEndPoint.getCurrentPlayers()) {
            playersJSON.add(name);
        }
        payload.add(JsonFields.PLAYERS, playersJSON);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateToolCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TOOL_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ToolCard card : this.gameEndPoint.getToolCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty(JsonFields.NAME, card.getName());
            jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
            jsonCard.addProperty(JsonFields.USED, card.isUsed());
            cards.add(jsonCard);
        }
        payload.add(JsonFields.TOOL_CARDS, cards);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void sendPublicObjectiveCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PUBLIC_OBJECTIVE_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ObjectiveCard card : this.gameEndPoint.getPublicObjectiveCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty(JsonFields.NAME, card.getName());
            jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
            jsonCard.addProperty(JsonFields.VICTORY_POINTS, card.getVictoryPoints());
            cards.add(jsonCard);
        }
        payload.add(JsonFields.PUBLIC_OBJECTIVE_CARDS, cards);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateWindowPatterns() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.WINDOW_PATTERNS.getString());
        JsonObject windowPatterns = new JsonObject();
        for (String player : this.gameEndPoint.getCurrentPlayers()) {
            windowPatterns.add(player, createWindowPatternJSON(this.gameEndPoint.getWindowPatternOf(player)));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatterns);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.DRAFT_POOL.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameEndPoint.getDraftPool()) {
            JsonObject die = new JsonObject();
            die.addProperty(JsonFields.COLOR, d.getColor().toString());
            die.addProperty(JsonFields.VALUE, d.getValue());
            die.addProperty(JsonFields.CLI_STRING, d.toString());
            dice.add(die);
        }
        payload.add(JsonFields.DICE, dice);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateRoundTrack(){
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ROUND_TRACK_DICE.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameEndPoint.getRoundTrackDice()) {
            JsonObject die = new JsonObject();
            die.addProperty(JsonFields.COLOR, d.getColor().toString());
            die.addProperty(JsonFields.VALUE, d.getValue());
            die.addProperty(JsonFields.CLI_STRING,d.toString());
            dice.add(die);
        }
        payload.add(JsonFields.DICE, dice);
        payload.addProperty(JsonFields.CLI_STRING, this.gameEndPoint.getRoundTrack().toString());
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void turnManagement() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TURN_MANAGEMENT.getString());
        payload.addProperty(JsonFields.CURRENT_ROUND, this.gameEndPoint.getCurrentRound());
        payload.addProperty(JsonFields.GAME_OVER, this.game.getRoundTrack().isGameOver());
        payload.addProperty(JsonFields.ACTIVE_PLAYER, this.gameEndPoint.getActivePlayer());
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    // UPDATE HANDLER

    @Override
    public void update(Observable o, Object arg) {
        String stringArg = String.valueOf(arg);
        if (o instanceof CountdownTimer) {
            if (stringArg.startsWith("WaitingRoom")) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                debug("Timer tick (from update): " + tick);
                this.updateTimerTick(tick);
            }
            // TODO "tm <tick>"
        } else if (o instanceof WaitingRoom) {
            if (arg instanceof Game) {
                this.game = (Game) arg;
                this.game.addObserver(this);
                this.gameEndPoint = new GameEndPoint(game);
                waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
                waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
                this.setupGame();
            } else if (arg instanceof List && uuid != null) {
                this.updateWaitingPlayersList((List<Player>) arg);
            }
        } else if (o instanceof Game) {
            if(stringArg.equals("$turnManagement$")) {
                updatePlayersList();
                updateToolCards();
                sendPublicObjectiveCards();
                updateWindowPatterns();
                updateDraftPool();
                turnManagement();
            }
            else if(stringArg.equals("$placeDie$")){
                updateWindowPatterns();
                updateDraftPool();
            }
            /*else if(stringArg.equals("$nextTurn$")){
                updateWindowPatterns();
                updateDraftPool();
            }*/
            else if(stringArg.equals("$roundTrack$")){
                //TODO
                updateRoundTrack();
                updateDraftPool();
            }
        } else if (stringArg.equals("$useToolCard$")) {
                updateToolCards();
                updateRoundTrack();
                updateWindowPatterns();
                updateDraftPool();
        }
    }

    private void updateTimerTick(int tick) {
        debug("updateTimerTick called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD,Methods.WR_TIMER_TICK.getString());
        payload.addProperty(JsonFields.TICK, tick);
        out.println(payload.toString());
        debug("Timer Tick Update sent");
    }

    private void updateWaitingPlayersList(List<Player> players) {
        debug("updateWaitingPlayersList called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.UPDATE_WAITING_PLAYERS.getString());
        JsonArray array = new JsonArray();
        for (Player p : players) array.add(p.getNickname());
        payload.add(JsonFields.PLAYERS, array);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        debug("Update Waiting Players List sent");
    }

    private JsonObject createWindowPatternJSON(WindowPattern wp) {
        JsonObject wpJSON = new JsonObject();
        wpJSON.addProperty(JsonFields.DIFFICULTY, wp.getDifficulty());
        JsonArray grid = new JsonArray();
        for (Cell c : wp.getGrid()) {
            JsonObject cellJSON = new JsonObject();
            cellJSON.addProperty(JsonFields.COLOR, c.getCellColor() != null ? c.getCellColor().toString() : null);
            cellJSON.addProperty(JsonFields.VALUE, c.getCellValue());
            JsonObject die = null;
            if (c.getPlacedDie() != null) {
                die = new JsonObject();
                die.addProperty(JsonFields.COLOR, c.getPlacedDie().getColor().toString());
                die.addProperty(JsonFields.VALUE, c.getPlacedDie().getValue());
            }
            cellJSON.add(JsonFields.DIE, die);
            grid.add(cellJSON);
        }
        wpJSON.add(JsonFields.GRID, grid);
        wpJSON.addProperty(JsonFields.CLI_STRING, wp.toString());
        return wpJSON;
    }

    private void setupGame() {
        debug("setupGame called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.GAME_SETUP.getString());
        Player player = this.gameEndPoint.getPlayer(uuid);
        ObjectiveCard privateObjectiveCard = player.getPrivateObjectiveCard();

        // Private objective card
        JsonObject privateObjectiveCardJSON = new JsonObject();
        privateObjectiveCardJSON.addProperty(JsonFields.NAME, privateObjectiveCard.getName());
        privateObjectiveCardJSON.addProperty(JsonFields.DESCRIPTION, privateObjectiveCard.getDescription());
        privateObjectiveCardJSON.addProperty(JsonFields.VICTORY_POINTS, privateObjectiveCard.getVictoryPoints());
        payload.add(JsonFields.PRIVATE_OBJECTIVE_CARD, privateObjectiveCardJSON);

        // Window Patterns
        List<WindowPattern> windowPatterns = player.getWindowPatternList();
        JsonArray windowPatternsJSON = new JsonArray();
        for (WindowPattern wp : windowPatterns) {
            windowPatternsJSON.add(createWindowPatternJSON(wp));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatternsJSON);

        out.println(payload.toString());

        log("A game has started for " + nickname);

        this.gameEndPoint.getPlayer(uuid).addObserver(this);
    }

}
