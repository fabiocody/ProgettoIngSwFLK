package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.ToolCard;
import it.polimi.ingsw.util.*;
import java.util.*;


public abstract class ServerNetwork implements Observer {

    String nickname;
    UUID uuid;
    GameController gameController;

    Thread probeThread;
    boolean probed = true;

    abstract void sendProbe();
    abstract void showDisconnectedUserMessage();

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

    private JsonObject createToolCardJson(ToolCard card) {
        JsonObject jsonCard = new JsonObject();
        jsonCard.addProperty(JsonFields.NAME, card.getName());
        jsonCard.addProperty(JsonFields.DESCRIPTION, card.getDescription());
        jsonCard.addProperty(JsonFields.USED, card.isUsed());
        return jsonCard;
    }

    void probeCheck() {
        while (Constants.INDEX_CONSTANT == Constants.INDEX_CONSTANT) {
            try {
                Thread.sleep(Constants.PROBE_TIMEOUT * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            Logger.debug("Checking probe for " + nickname);
            if (!probed) {
                Logger.error("Probe error");
                this.onUserDisconnection();
                Thread.currentThread().interrupt();
            } else {
                probed = false;
                this.sendProbe();
            }
        }
    }

    void onUserDisconnection() {
        WaitingRoomController.getInstance().removePlayer(this.nickname);
        WaitingRoomController.getInstance().removeServerNetwork(this);
        if (gameController != null) {
            gameController.suspendPlayer(this.uuid);
            gameController.removeServerNetwork(this);
        }
        this.showDisconnectedUserMessage();
    }

    JsonObject updatePlayersList() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLAYERS.getString());
        JsonArray playersJSON = new JsonArray();
        for (String name : this.gameController.getCurrentPlayers()) {
            playersJSON.add(name);
        }
        payload.add(JsonFields.PLAYERS, playersJSON);
        Logger.debugPayload(payload);
        return payload;
    }

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

    JsonObject sendPublicObjectiveCards() {
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
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject updateWindowPatterns() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.WINDOW_PATTERNS.getString());
        JsonObject windowPatterns = new JsonObject();
        for (String player : this.gameController.getCurrentPlayers()) {
            windowPatterns.add(player, createWindowPatternJSON(this.gameController.getWindowPatternOf(player)));
        }
        payload.add(JsonFields.WINDOW_PATTERNS, windowPatterns);
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject updateFavorTokens() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FAVOR_TOKENS.getString());
        JsonObject favorTokens = new JsonObject();
        for (String player : this.gameController.getCurrentPlayers()) {
            favorTokens.addProperty(player, this.gameController.getFavorTokensOf(player));
        }
        payload.add(JsonFields.FAVOR_TOKENS, favorTokens);
        Logger.debugPayload(payload);
        return payload;
    }

    private JsonObject generateJsonDie(Die d) {
        JsonObject jsonDie = new JsonObject();
        jsonDie.addProperty(JsonFields.COLOR, d.getColor().toString());
        jsonDie.addProperty(JsonFields.VALUE, d.getValue());
        jsonDie.addProperty(JsonFields.CLI_STRING, d.toString());
        return jsonDie;
    }

    JsonObject updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.DRAFT_POOL.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getDraftPool()) {
            dice.add(generateJsonDie(d));
        }
        payload.add(JsonFields.DICE, dice);
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject updateRoundTrack() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ROUND_TRACK.getString());
        JsonArray dice = new JsonArray();
        for (Die d : this.gameController.getRoundTrackDice()) {
            dice.add(generateJsonDie(d));
        }
        payload.add(JsonFields.DICE, dice);
        payload.addProperty(JsonFields.CLI_STRING, this.gameController.getRoundTrack().toString());
        Logger.debugPayload(payload);
        return payload;
    }

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

    JsonObject updateFinalScores() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.FINAL_SCORES.getString());
        JsonObject finalScores = new JsonObject();
        for (String player : this.gameController.getFinalScores().keySet()) {
            finalScores.addProperty(player, this.gameController.getFinalScores().get(player));
        }
        payload.add(JsonFields.FINAL_SCORES, finalScores);
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject updateTimerTick(Methods method, String tick) {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, method.getString());
        payload.addProperty(JsonFields.TICK, tick);
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject updateWaitingPlayers() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.UPDATE_WAITING_PLAYERS.getString());
        JsonArray array = new JsonArray();
        for (String p : WaitingRoomController.getInstance().getWaitingPlayers()) array.add(p);
        payload.add(JsonFields.PLAYERS, array);
        Logger.debugPayload(payload);
        return payload;
    }

    JsonObject setupGame() {
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
        Logger.debugPayload(payload);
        Logger.log("A game has started for " + this.nickname);
        this.gameController.getPlayer(this.uuid).addObserver(this);
        return payload;
    }

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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SagradaServer) {
            WaitingRoomController.getInstance().removeServerNetwork(this);
            this.gameController = (GameController) arg;
            this.gameController.addServerNetwork(this);
            this.setupGame();
        }
    }

}
