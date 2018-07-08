package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.ToolCard;
import it.polimi.ingsw.shared.util.*;
import java.util.*;


/**
 * the base class for the serverRMIHandler and the serverSocketHandler
 */
public abstract class ServerNetwork extends Observable implements Observer {

    private String nickname;
    private UUID uuid;
    private GameController gameController;
    private Thread probeThread;
    private boolean probed = true;
    private JsonParser jsonParser;

    /**
     * @return the nickname
     */
    String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the string to which the nickname will be set
     */
    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the uuid
     */
    UUID getUUID() {
        return uuid;
    }

    /**
     * @param uuid the uuid to which the uuid will be set
     */
    void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the game controller
     */
    GameController getGameController() {
        return gameController;
    }

    /**
     * @param gameController the game controller
     */
    void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * This method starts the thread that sends the probes
     */
    void startProbeThread() {
        probeThread = new Thread(this::probeCheck);
        probeThread.start();
    }

    /**
     * This method sets the probe attribute to true
     */
    void setProbed() {
        this.probed = true;
    }

    /**
     * @return the JsonParser, and creates it if it was null
     */
    JsonParser getJsonParser() {
        if (jsonParser == null)
            jsonParser = new JsonParser();
        return jsonParser;
    }

    abstract void sendProbe();
    abstract void showDisconnectedUserMessage();
    abstract void close();

    /**
     * This method creates a JsonObject containing the information of a window pattern
     *
     * @param wp the window pattern
     * @return the JsonObject
     */
    private JsonObject createWindowPatternJson(WindowPattern wp) {
        JsonObject wpJSON = new JsonObject();
        wpJSON.addProperty(JsonFields.NAME, wp.getPatternName());
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

    /**
     * This method creates a JsonObject containing the information of a tool card
     *
     * @param card the tool card
     * @return the JsonObject
     */
    private JsonObject createToolCardJson(ToolCard card) {
        JsonObject jsonCard = new JsonObject();
        jsonCard.addProperty(JsonFields.NAME, card.getName());
        jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
        jsonCard.addProperty(JsonFields.USED, card.isUsed());
        return jsonCard;
    }

    /**
     * This method creates a JsonObject containing the information of a public objective card
     *
     * @param card the public objective card
     * @return the JsonObject
     */
    JsonObject createObjectiveCardJson(ObjectiveCard card) {
        JsonObject jsonCard = new JsonObject();
        jsonCard.addProperty(JsonFields.NAME, card.getName());
        jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
        jsonCard.addProperty(JsonFields.VICTORY_POINTS, card.getVictoryPoints());
        return jsonCard;
    }

    /**
     * This method creates a JsonObject containing the information of a die
     *
     * @param d the die
     * @return the JsonObject
     */
    private JsonObject createDieJson(Die d) {
        JsonObject jsonDie = new JsonObject();
        jsonDie.addProperty(JsonFields.COLOR, d.getColor().toString());
        jsonDie.addProperty(JsonFields.VALUE, d.getValue());
        jsonDie.addProperty(JsonFields.CLI_STRING, d.toString());
        return jsonDie;
    }

    /**
     * This method probes the player periodically, and disconnects him if he isn't probed correctly
     */
    private void probeCheck() {
        while (true) {
            try {
                Thread.sleep(Constants.PROBE_TIMEOUT * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            Logger.debug("Checking probe for " + nickname);
            if (!probed) {
                Logger.error("Probe error");
                this.onUserDisconnection();
                Thread.currentThread().interrupt();
                return;
            } else {
                probed = false;
                this.sendProbe();
            }
        }
    }

    /**
     * This method is used to disconnect the player by removing it from the waiting room
     * or suspending it if it's already in a game
     */
    void onUserDisconnection() {
        if (probeThread != null) probeThread.interrupt();
        SagradaServer.getInstance().deleteObserver(this);
        WaitingRoomController.getInstance().removePlayer(this.nickname);
        WaitingRoomController.getInstance().removeNetwork(this);
        if (gameController != null) {
            gameController.suspendPlayer(this.uuid);
            gameController.removeNetwork(this);
        }
        setChanged();
        notifyObservers(nickname);
        this.showDisconnectedUserMessage();
    }

    /**
     * @return JsonObject containing the information necessary for a players list update
     */
    JsonObject updatePlayersList() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLAYERS.getString());
        JsonArray playersJSON = new JsonArray();
        for (String name : this.gameController.getPlayers()) {
            playersJSON.add(name);
        }
        payload.add(JsonFields.PLAYERS, playersJSON);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a tool card update
     */
    JsonObject updateToolCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TOOL_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ToolCard card : this.gameController.getToolCards())
            cards.add(createToolCardJson(card));
        payload.add(JsonFields.TOOL_CARDS, cards);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information of the public objective cards
     */
    JsonObject sendPublicObjectiveCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PUBLIC_OBJECTIVE_CARDS.getString());
        JsonArray cards = new JsonArray();
        for (ObjectiveCard card : this.gameController.getPublicObjectiveCards()) {
            JsonObject jsonCard = createObjectiveCardJson(card);
            cards.add(jsonCard);
        }
        payload.add(JsonFields.PUBLIC_OBJECTIVE_CARDS, cards);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a window pattern update
     */
    JsonObject updateWindowPatterns() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.WINDOW_PATTERNS.getString());
        JsonObject windowPatterns = new JsonObject();
        for (String player : this.gameController.getPlayers()) {
            windowPatterns.add(player, createWindowPatternJson(this.gameController.getWindowPattern(player)));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatterns);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a favor token update
     */
    JsonObject updateFavorTokens() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FAVOR_TOKENS.getString());
        JsonObject favorTokens = new JsonObject();
        for (String player : this.gameController.getPlayers()) {
            favorTokens.addProperty(player, this.gameController.getFavorTokens(player));
        }
        payload.add(JsonFields.FAVOR_TOKENS, favorTokens);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a draft pool update
     */
    JsonObject updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.DRAFT_POOL.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getDraftPool()) {
            dice.add(createDieJson(d));
        }
        payload.add(JsonFields.DICE, dice);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a round track update
     */
    JsonObject updateRoundTrack() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ROUND_TRACK.getString());
        JsonArray dice = new JsonArray();
        for (List<Die> round : this.gameController.getRoundTrackDice()) {
            JsonArray roundArray = new JsonArray();
            for (Die die : round)
                roundArray.add(createDieJson(die));
            dice.add(roundArray);
        }
        payload.add(JsonFields.DICE, dice);
        payload.addProperty(JsonFields.CLI_STRING, this.gameController.getRoundTrack().toString());
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a turn manager update
     */
    JsonObject turnManagement() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.TURN_MANAGEMENT.getString());
        payload.addProperty(JsonFields.CURRENT_ROUND, this.gameController.getCurrentRound());
        payload.addProperty(JsonFields.GAME_OVER, this.gameController.getRoundTrack().isGameOver());
        payload.addProperty(JsonFields.ACTIVE_PLAYER, this.gameController.getActivePlayer());
        JsonArray suspendedPlayers = new JsonArray();
        this.gameController.getSuspendedPlayers().forEach(suspendedPlayers::add);
        payload.add(JsonFields.SUSPENDED_PLAYERS, suspendedPlayers);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a final score update
     */
    JsonObject updateFinalScores() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FINAL_SCORES.getString());
        JsonObject finalScores = new JsonObject();
        for (String player : this.gameController.getFinalScores().keySet()) {
            if (player.equals(JsonFields.WINNER))
                finalScores.addProperty(player, gameController.getFinalScores().get(player).getNickname());
            else
                finalScores.addProperty(player, gameController.getFinalScores().get(player).getFinalScore());
        }
        payload.add(JsonFields.FINAL_SCORES, finalScores);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @param method can be WR_TIMER_TICK or GAME_TIMER_TICK depending on what timer we want to update
     * @param tick the remaining time
     * @return JsonObject containing the information necessary for a timer update
     */
    JsonObject updateTimerTick(Methods method, String tick) {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, method.getString());
        payload.addProperty(JsonFields.TICK, tick);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for waiting players update
     */
    JsonObject updateWaitingPlayers() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.UPDATE_WAITING_PLAYERS.getString());
        JsonArray array = new JsonArray();
        for (String p : WaitingRoomController.getInstance().getWaitingPlayers()) array.add(p);
        payload.add(JsonFields.PLAYERS, array);
        Logger.debugPayload(payload);
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for the game setup
     */
    JsonObject setupGame() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.GAME_SETUP.getString());
        Player player = this.gameController.getPlayer(this.uuid);
        ObjectiveCard privateObjectiveCard = player.getPrivateObjectiveCard();
        // Private objective card
        JsonObject privateObjectiveCardJSON = createObjectiveCardJson(privateObjectiveCard);
        payload.add(JsonFields.PRIVATE_OBJECTIVE_CARD, privateObjectiveCardJSON);
        // Window Patterns
        List<WindowPattern> windowPatterns = player.getWindowPatternList();
        JsonArray windowPatternsJSON = new JsonArray();
        for (WindowPattern wp : windowPatterns) {
            windowPatternsJSON.add(createWindowPatternJson(wp));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatternsJSON);
        Logger.debugPayload(payload);
        Logger.log("A game has started for " + this.nickname);
        this.gameController.getPlayer(this.uuid).addObserver(this);
        return payload;
    }

    /**
     * This method is used to send a full update
     */
    void fullUpdate() {
        updatePlayersList();
        updateToolCards();
        sendPublicObjectiveCards();
        updateWindowPatterns();
        updateFavorTokens();
        updateDraftPool();
        updateRoundTrack();
        turnManagement();
    }

    /**
     * Observer method. Removes the waiting room controller, adds the game controller to the list of networks,
     * and sets up the game
     *
     * @param o the object that has triggered the update
     * @param arg the arguments of the update
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SagradaServer) {
            WaitingRoomController.getInstance().removeNetwork(this);
            this.gameController = (GameController) arg;
            this.gameController.addNetwork(this);
            this.setupGame();
        }
    }

}
