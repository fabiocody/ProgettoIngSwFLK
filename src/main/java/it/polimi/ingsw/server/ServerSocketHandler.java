package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class ServerSocketHandler extends ServerNetwork implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonParser jsonParser;
    private boolean run = true;

    public ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
        this.jsonParser = new JsonParser();
    }

    @Override
    void showDisconnectedUserMessage() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        Logger.println("Disconnected " + address + " (nickname: " + this.nickname + ")");
        run = false;
        Thread.currentThread().interrupt();
    }

    // REQUESTS HANDLER

    @Override
    public void run() {
        try (Socket socket = this.clientSocket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true)) {
            Logger.println("Connected to " + socket.getInetAddress() + ":" + socket.getPort() + " via socket");
            while (this.run) {
                JsonObject input;
                try {
                    input = this.parseJson(this.readLine());
                } catch (NullPointerException e) {
                    Logger.debug("JSON parsing failed");
                    continue;
                }
                Logger.debug("Received: " + input.toString());
                // UUID validation
                if (!input.get(JsonFields.METHOD).getAsString().equals(Methods.ADD_PLAYER.getString()) &&
                        !input.get(JsonFields.PLAYER_ID).getAsString().equals(this.uuid.toString())) {
                    Logger.error("AUTH ERROR");
                    continue;
                }
                Methods calledMethod;
                try {
                    calledMethod = Methods.getAsMethods(input.get(JsonFields.METHOD).getAsString());
                } catch (NoSuchElementException e) {
                    Logger.error("METHOD NOT RECOGNIZED");
                    continue;
                }
                Logger.debug("Received method " + calledMethod.getString());
                switch (calledMethod) {
                    case ADD_PLAYER:
                        this.addPlayer(input);
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
                    case REQUIRED_DATA:
                        this.requiredData(input);
                        break;
                    case PROBE:
                        probed = true;
                        break;
                    default:
                        Logger.error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (Exception e) {
            Logger.error("run");
            this.onUserDisconnection();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.onUserDisconnection();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
        }
        return line;
    }

    private JsonObject parseJson(String string) {
        Logger.debug("Parsing JSON");
        return this.jsonParser.parse(string).getAsJsonObject();
    }

    private void addPlayer(JsonObject input) {
        Logger.debug("addPlayer called");
        Logger.debug("INPUT " + input.toString());
        String tempNickname = input.get(JsonFields.NICKNAME).getAsString();
        try {
            this.uuid = WaitingRoomController.getInstance().addPlayer(tempNickname);
            this.nickname = tempNickname;
            Logger.println(this.nickname + " logged in successfully (" + this.uuid + ")");
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, false);
            payload.addProperty(JsonFields.PLAYER_ID, this.uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (String p : WaitingRoomController.getInstance().getWaitingPlayers())
                waitingPlayers.add(p);
            payload.add(JsonFields.PLAYERS, waitingPlayers);
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            Logger.println("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, false);
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (NicknameAlreadyUsedInGameException e) {
            Game game = e.getGame();
            Optional<GameController> optionalGameController = SagradaServer.getInstance().getGameControllers().stream()
                    .filter(controller -> controller.getGame() == game)
                    .findFirst();
            this.gameController = optionalGameController.orElse(null);
            this.nickname = tempNickname;
            Player player = game.getPlayerForNickname(this.nickname);
            this.uuid = player.getId();
            Logger.println(this.nickname + " logged back in (" + this.uuid + ")");
            //game.addObserver(this);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, true);
            payload.addProperty(JsonFields.PLAYER_ID, this.uuid.toString());
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
            gameController.unsuspendPlayer(this.uuid);
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    fullUpdate();
                }
            }, 500);
        }
    }

    private void choosePattern(JsonObject input) {
        int patternIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.PATTERN_INDEX).getAsInt();
        this.gameController.choosePattern(this.uuid, patternIndex);
        Logger.log(this.nickname + " has chosen pattern " + patternIndex);
    }

    private void placeDie(JsonObject input) {
        int draftPoolIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int x = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_X).getAsInt();
        int y = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_Y).getAsInt();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLACE_DIE.getString());
        try {
            this.gameController.placeDie(this.uuid, draftPoolIndex, x, y);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.println(this.nickname + " placed a die");
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            payload.addProperty(JsonFields.RESULT, false);
            Logger.println(this.nickname + " die placement was refused");
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        }
    }

    private void useToolCard(JsonObject input) {
        int cardIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.CARD_INDEX).getAsInt();
        JsonObject data = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DATA).getAsJsonObject();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        boolean tax;
        tax = !(input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE));
        data.addProperty(JsonFields.PLAYER_ID, id.toString());
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, JsonFields.USE_TOOL_CARD);
        try {
            this.gameController.useToolCard(id, cardIndex, data);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.println(this.nickname + " used a tool card");
            if (tax) {      //TODO spostare nel gameController
                if (!gameController.getToolCards().get(cardIndex).isUsed()) {
                    this.gameController.getPlayer(id).setFavorTokens(this.gameController.getPlayer(id).getFavorTokens() - 1);
                    Logger.debug("removed 1 favor token");
                } else {
                    this.gameController.getPlayer(id).setFavorTokens(this.gameController.getPlayer(id).getFavorTokens() - 2);
                    Logger.debug("removed 2 favor tokens");
                }
            }
            if (!(input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE))) {
                this.gameController.getToolCards().get(cardIndex).setUsed();
            }
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            e.printStackTrace();        // TODO Remove
            payload.addProperty(JsonFields.RESULT, false);
            Logger.println(this.nickname + " usage of tool card was refused");
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        }
    }

    private void requiredData(JsonObject input) {
        int cardIndex = input.get(JsonFields.CARD_INDEX).getAsInt();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        JsonObject payload = this.gameController.requiredData(cardIndex);
        if ((this.gameController.getPlayer(id).getFavorTokens()<2 && gameController.getToolCards().get(cardIndex).isUsed()) ||
                (this.gameController.getPlayer(id).getFavorTokens()<1 && !gameController.getToolCards().get(cardIndex).isUsed())){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS, Constants.INDEX_CONSTANT);
        }
        if (payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX) && this.gameController.getRoundTrack().getAllDice().isEmpty()){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.ROUND_TRACK);
        }
        if ((payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) &&
                (!(payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X))) && (this.gameController.getPlayer(id).isDiePlacedInThisTurn()) && (!payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.SECOND_DIE_PLACEMENT))) {
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.DIE);
        }
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void nextTurn() {
        this.gameController.nextTurn();
        Logger.println(this.nickname + " has ended his turn");
    }

    @Override
    void updatePlayersList() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLAYERS.getString());
        JsonArray playersJSON = new JsonArray();
        for (String name : this.gameController.getCurrentPlayers()) {
            playersJSON.add(name);
        }
        payload.add(JsonFields.PLAYERS, playersJSON);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void updateToolCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TOOL_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ToolCard card : this.gameController.getToolCards())
            cards.add(createToolCardJson(card));
        payload.add(JsonFields.TOOL_CARDS, cards);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void sendPublicObjectiveCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PUBLIC_OBJECTIVE_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ObjectiveCard card : this.gameController.getPublicObjectiveCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty(JsonFields.NAME, card.getName());
            jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
            jsonCard.addProperty(JsonFields.VICTORY_POINTS, card.getVictoryPoints());
            cards.add(jsonCard);
        }
        payload.add(JsonFields.PUBLIC_OBJECTIVE_CARDS, cards);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void updateWindowPatterns() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.WINDOW_PATTERNS.getString());
        JsonObject windowPatterns = new JsonObject();
        for (String player : this.gameController.getCurrentPlayers()) {
            windowPatterns.add(player, createWindowPatternJSON(this.gameController.getWindowPatternOf(player)));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatterns);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void updateFavorTokens() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FAVOR_TOKENS.getString());
        JsonObject favorTokens = new JsonObject();
        for (String player : this.gameController.getCurrentPlayers()) {
            favorTokens.addProperty(player, this.gameController.getFavorTokensOf(player));
        }
        payload.add(JsonFields.FAVOR_TOKENS, favorTokens);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private JsonObject generateJsonDie(Die d) {
        JsonObject jsonDie = new JsonObject();
        jsonDie.addProperty(JsonFields.COLOR, d.getColor().toString());
        jsonDie.addProperty(JsonFields.VALUE, d.getValue());
        jsonDie.addProperty(JsonFields.CLI_STRING, d.toString());
        return jsonDie;
    }

    @Override
    void updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.DRAFT_POOL.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getDraftPool()) {
            dice.add(generateJsonDie(d));
        }
        payload.add(JsonFields.DICE, dice);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void updateRoundTrack() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ROUND_TRACK.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getRoundTrackDice()) {
            dice.add(generateJsonDie(d));
        }
        payload.add(JsonFields.DICE, dice);
        payload.addProperty(JsonFields.CLI_STRING, this.gameController.getRoundTrack().toString());
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void turnManagement() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TURN_MANAGEMENT.getString());
        payload.addProperty(JsonFields.CURRENT_ROUND, this.gameController.getCurrentRound());
        payload.addProperty(JsonFields.GAME_OVER, this.gameController.getRoundTrack().isGameOver());
        payload.addProperty(JsonFields.ACTIVE_PLAYER, this.gameController.getActivePlayer());
        JsonArray suspendedPlayers = new JsonArray();
        this.gameController.getSuspendedPlayers().forEach(suspendedPlayers::add);
        payload.add(JsonFields.SUSPENDED_PLAYERS, suspendedPlayers);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    @Override
    void updateFinalScores() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FINAL_SCORES.getString());
        JsonObject finalScores = new JsonObject();
        for (String player : this.gameController.getFinalScores().keySet()) {
            finalScores.addProperty(player, this.gameController.getFinalScores().get(player));
        }
        payload.add(JsonFields.FINAL_SCORES, finalScores);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    // UPDATE HANDLER

    @Override
    void updateTimerTick(Methods method, String tick) {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, method.getString());
        payload.addProperty(JsonFields.TICK, tick);
        out.println(payload.toString());
        Logger.debug("updateTimerTick sent to" + nickname);
    }

    @Override
    void updateWaitingPlayers(List<String> players) {
        Logger.debug("updateWaitingPlayers called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.UPDATE_WAITING_PLAYERS.getString());
        JsonArray array = new JsonArray();
        for (String p : players) array.add(p);
        payload.add(JsonFields.PLAYERS, array);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        Logger.debug("Update Waiting Players List sent");
    }

    @Override
    void setupGame() {
        Logger.debug("setupGame called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.GAME_SETUP.getString());
        Player player = this.gameController.getPlayer(this.uuid);
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

        Logger.println("A game has started for " + this.nickname);

        this.gameController.getPlayer(this.uuid).addObserver(this);
    }

    @Override
    void fullUpdate() {
        //if (!this.gameController.getRoundTrack().isGameOver()) {
            updatePlayersList();
            updateToolCards();
            sendPublicObjectiveCards();
            updateWindowPatterns();
            updateFavorTokens();
            updateDraftPool();
            updateRoundTrack();
            turnManagement();
        //}
    }

    @Override
    void sendProbe() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PROBE.getString());
        out.println(payload.toString());
    }
}
