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
import java.rmi.RemoteException;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {
    // Observes CountdownTimer (from WaitingRoom and TurnManager), WaitingRoom and Game

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private GameController gameController;
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
        WaitingRoomController.getInstance().subscribeToWaitingRoom(this);
    }

    private void probe() {
        while (Constants.INDEX_CONSTANT == Constants.INDEX_CONSTANT) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                break;
            }
            if (!probed) {
                Logger.error("Probe error");
                notifyDisconnectedUser();
                Thread.currentThread().interrupt();
            }
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.PROBE.getString());
            out.println(payload.toString());
            probed = false;
        }
    }

    private void notifyDisconnectedUser() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        Logger.println("Disconnected " + address + " (nickname: " + nickname + ")");
        WaitingRoomController.getInstance().removePlayer(nickname);
        WaitingRoomController.getInstance().unsubscribeFromWaitingRoom(this);
        WaitingRoomController.getInstance().unsubscribeFromWaitingRoomTimer(this);
        if (gameController != null) {
            gameController.unsubscribeFromTurnManagerTimer(this);
            gameController.suspendPlayer(uuid);
        }
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
                    case SUBSCRIBE_TO_WR_TIMER:
                        this.subscribeToWRTimer();
                        break;
                    case SUBSCRIBE_TO_GAME_TIMER:
                        // TODO
                        this.subscribeToGameTimer();
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
            e.printStackTrace();
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.notifyDisconnectedUser();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
            WaitingRoomController.getInstance().unsubscribeFromWaitingRoom(this);
            WaitingRoomController.getInstance().unsubscribeFromWaitingRoomTimer(this);
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
            uuid = WaitingRoomController.getInstance().addPlayer(tempNickname);
            nickname = tempNickname;
            Logger.println(nickname + " logged in (" + uuid + ")");
            Logger.debug("UUID: " + uuid.toString());
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, false);
            payload.addProperty(JsonFields.PLAYER_ID, uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (Player p : WaitingRoomController.getInstance().getWaitingPlayers())
                waitingPlayers.add(p.getNickname());
            payload.add(JsonFields.PLAYERS, waitingPlayers);
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
            this.probeThread = new Thread(this::probe);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            Logger.println("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, false);
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (NicknameAlreadyUsedInGameException e) {
            game = e.getGame();
            nickname = tempNickname;
            Player player = game.getPlayerForNickname(nickname);
            uuid = player.getId();
            Logger.println(nickname + " logged back in (" + uuid + ")");
            this.game.addObserver(this);
            this.gameController = new GameController(game);
            WaitingRoomController.getInstance().unsubscribeFromWaitingRoom(this);
            WaitingRoomController.getInstance().unsubscribeFromWaitingRoomTimer(this);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, true);
            payload.addProperty(JsonFields.PLAYER_ID, uuid.toString());
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
            this.probeThread = new Thread(this::probe);
            this.probeThread.start();
            gameController.unsuspendPlayer(uuid);
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    fullUpdate();
                }
            }, 500);
        }
    }

    private void subscribeToWRTimer() {
        WaitingRoomController.getInstance().subscribeToWaitingRoomTimer(this);
        Logger.debug("WR timer registered");
        /*JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.SUBSCRIBE_TO_WR_TIMER.getString());
        payload.addProperty(JsonFields.RESULT, true);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());*/
    }

    private void subscribeToGameTimer() {
        this.gameController.subscribeToTurnManagerTimer(this);
        Logger.debug("Game timer registered");
        /*JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.SUBSCRIBE_TO_GAME_TIMER.getString());
        payload.addProperty(JsonFields.RESULT, true);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        Logger.debug(Methods.SUBSCRIBE_TO_GAME_TIMER.getString() + " sent");*/
    }

    private void choosePattern(JsonObject input) {
        int patternIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.PATTERN_INDEX).getAsInt();
        this.gameController.choosePattern(uuid, patternIndex);
        Logger.println(nickname + " has chosen pattern " + patternIndex);
    }

    private void placeDie(JsonObject input) {
        int draftPoolIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int x = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_X).getAsInt();
        int y = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_Y).getAsInt();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLACE_DIE.getString());
        try {
            this.gameController.placeDie(uuid, draftPoolIndex, x, y);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.println(nickname + " placed a die");
            Logger.debug("PAYLOAD " + payload.toString());
            out.println(payload.toString());
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            payload.addProperty(JsonFields.RESULT, false);
            Logger.println(nickname + " die placement was refused");
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
            Logger.println(nickname + " used a tool card");
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
        } catch (RemoteException | InvalidEffectArgumentException | InvalidEffectResultException e) {
            e.printStackTrace();        // TODO Remove
            payload.addProperty(JsonFields.RESULT, false);
            Logger.println(nickname + " usage of tool card was refused");
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
        Logger.println(nickname + " has ended his turn");
    }

    private void updatePlayersList() {
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

    private void updateToolCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TOOL_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ToolCard card : this.gameController.getToolCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty(JsonFields.NAME, card.getName());
            jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
            jsonCard.addProperty(JsonFields.USED, card.isUsed());
            cards.add(jsonCard);
        }
        payload.add(JsonFields.TOOL_CARDS, cards);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void sendPublicObjectiveCards() {
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

    private void updateWindowPatterns() {
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

    private void updateFavorTokens() {
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

    private void updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.DRAFT_POOL.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getDraftPool()) {
            JsonObject die = new JsonObject();
            die.addProperty(JsonFields.COLOR, d.getColor().toString());
            die.addProperty(JsonFields.VALUE, d.getValue());
            die.addProperty(JsonFields.CLI_STRING, d.toString());
            dice.add(die);
        }
        payload.add(JsonFields.DICE, dice);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateRoundTrack() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ROUND_TRACK_DICE.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getRoundTrackDice()) {
            JsonObject die = new JsonObject();
            die.addProperty(JsonFields.COLOR, d.getColor().toString());
            die.addProperty(JsonFields.VALUE, d.getValue());
            die.addProperty(JsonFields.CLI_STRING, d.toString());
            dice.add(die);
        }
        payload.add(JsonFields.DICE, dice);
        payload.addProperty(JsonFields.CLI_STRING, this.gameController.getRoundTrack().toString());
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void turnManagement() {
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

    private void updateFinalScores() {
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
    public void update(Observable o, Object arg) {
        String stringArg = String.valueOf(arg);
        if (o instanceof CountdownTimer) {
            if (stringArg.startsWith(NotificationsMessages.WAITING_ROOM)) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                Logger.debug("WR Timer tick (from update): " + tick);
                this.updateTimerTick(Methods.WR_TIMER_TICK, tick);
            } else if (stringArg.startsWith(NotificationsMessages.TURN_MANAGER)) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                Logger.debug("Game Timer tick (from update): " + tick);
                this.updateTimerTick(Methods.GAME_TIMER_TICK, tick);
            }
        } else if (o instanceof WaitingRoom) {
            if (arg instanceof Game) {
                this.game = (Game) arg;
                this.game.addObserver(this);
                this.gameController = new GameController(game);
                WaitingRoomController.getInstance().unsubscribeFromWaitingRoom(this);
                WaitingRoomController.getInstance().unsubscribeFromWaitingRoomTimer(this);
                this.setupGame();
            } else if (arg instanceof List && uuid != null) {
                this.updateWaitingPlayersList((List<Player>) arg);
            }
        } else if (o instanceof Game) {
            switch (stringArg) {
                case NotificationsMessages.TURN_MANAGEMENT:
                case NotificationsMessages.SUSPENDED:
                case NotificationsMessages.PLACE_DIE:
                case NotificationsMessages.USE_TOOL_CARD:
                    //System.out.println(ansi().fgGreen().a(stringArg).reset());
                    fullUpdate();
                    break;
                case NotificationsMessages.GAME_OVER:
                    updateFinalScores();
                    turnManagement();
                    break;
            }
        }
    }

    private void updateTimerTick(Methods method, int tick) {
        Logger.debug("updateTimerTick called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, method.getString());
        payload.addProperty(JsonFields.TICK, tick);
        out.println(payload.toString());
        Logger.debug("Timer Tick Update sent");
    }

    private void updateWaitingPlayersList(List<Player> players) {
        Logger.debug("updateWaitingPlayersList called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.UPDATE_WAITING_PLAYERS.getString());
        JsonArray array = new JsonArray();
        for (Player p : players) array.add(p.getNickname());
        payload.add(JsonFields.PLAYERS, array);
        Logger.debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        Logger.debug("Update Waiting Players List sent");
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
        Logger.debug("setupGame called");
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.GAME_SETUP.getString());
        Player player = this.gameController.getPlayer(uuid);
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

        Logger.println("A game has started for " + nickname);

        this.gameController.getPlayer(uuid).addObserver(this);
    }

    private void fullUpdate() {
        if (!this.gameController.getRoundTrack().isGameOver()) {
            updatePlayersList();
            updateToolCards();
            sendPublicObjectiveCards();
            updateWindowPatterns();
            updateFavorTokens();
            updateDraftPool();
            updateRoundTrack();
            turnManagement();
        }
    }
}



