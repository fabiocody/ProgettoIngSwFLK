package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.toolcards.InvalidEffectArgumentException;
import it.polimi.ingsw.model.toolcards.InvalidEffectResultException;
import it.polimi.ingsw.model.toolcards.ToolCard;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.util.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class ServerRMIHandler extends ServerNetwork implements ServerAPI {

    ClientAPI client;

    ServerRMIHandler(ClientAPI client) {
        this.client = client;
        if (this.client != null) WaitingRoomController.getInstance().addServerNetwork(this);
    }

    ServerRMIHandler() {
        this(null);
    }

    @Override
    public ServerAPI connect(ClientAPI client) {
        Logger.println("User connected via RMI");
        try {
            ServerNetwork remoteServer = new ServerRMIHandler(client);
            SagradaServer.getInstance().addObserver(remoteServer);
            return (ServerAPI) UnicastRemoteObject.exportObject((ServerAPI) remoteServer, 0);
        } catch (RemoteException e) {
            Logger.error("Cannot export new ServerAPI");
            return null;
        }
    }

    @Override
    public void probe() {
        Logger.debug(nickname + " has sent a probe message");
        probed = true;
    }

    @Override
    public UUID addPlayer(String nickname) {
        Logger.debug(nickname + " is attempting to log in");
        uuid = null;
        try {
            uuid = WaitingRoomController.getInstance().addPlayer(nickname);
            this.nickname = nickname;
            Logger.println(nickname + " logged in successfully (" + uuid + ")");
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            Logger.error(nickname + " log in failed");
        } catch (NicknameAlreadyUsedInGameException e) {
            Logger.println("Welcome back " + nickname);
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
        }
        return uuid;
    }

    @Override
    public void choosePattern(int patternIndex) {
        gameController.choosePattern(uuid, patternIndex);
        Logger.log(this.nickname + " has chosen pattern " + patternIndex);
    }

    @Override
    public boolean placeDie(int draftPoolIndex, int x, int y) {
        try {
            this.gameController.placeDie(this.uuid, draftPoolIndex, x, y);
            Logger.log(this.nickname + " placed a die");
            return true;
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            Logger.log(this.nickname + "'s die placement attempt was refused");
            return false;
        }
    }

    @Override
    public void nextTurn() {
        gameController.nextTurn();
        Logger.log(this.nickname + " has ended his turn");
    }

    @Override
    public JsonObject requiredData(int cardIndex) {
        JsonObject payload = this.gameController.requiredData(cardIndex);
        Player player = this.gameController.getPlayer(uuid);
        if ((player.getFavorTokens()<2 && gameController.getToolCards().get(cardIndex).isUsed()) ||
                (player.getFavorTokens()<1 && !gameController.getToolCards().get(cardIndex).isUsed())){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS, Constants.INDEX_CONSTANT);
        }
        if (payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX) && this.gameController.getRoundTrack().getAllDice().isEmpty()){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.ROUND_TRACK);
        }
        if ((payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) &&
                (!(payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X))) && (player.isDiePlacedInThisTurn()) && (!payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.SECOND_DIE_PLACEMENT))) {
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.DIE);
        }
        return payload;
    }

    @Override
    public boolean useToolCard(int cardIndex, JsonObject requiredData) {
        requiredData.addProperty(JsonFields.PLAYER_ID, uuid.toString());
        try {
            this.gameController.useToolCard(uuid, cardIndex, requiredData);
            Logger.println(this.nickname + " used a tool card");
            return true;
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            //e.printStackTrace();
            Logger.println(this.nickname + " usage of tool card was refused");
            return false;
        }
    }


    @Override
    void updatePlayersList() {
        try {
            client.updatePlayersList(gameController.getCurrentPlayers());
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    void updateToolCards() {
        List<String> cards = new ArrayList<>();
        for (ToolCard card : this.gameController.getToolCards())
            cards.add(createToolCardJson(card).toString());
        try {
            client.updateToolCards(cards);
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    void sendPublicObjectiveCards() {

    }

    @Override
    void updateWindowPatterns() {

    }

    @Override
    void updateFavorTokens() {

    }

    @Override
    void updateDraftPool() {

    }

    @Override
    void updateRoundTrack() {

    }

    @Override
    void turnManagement() {

    }

    @Override
    void updateFinalScores() {

    }

    @Override
    void updateTimerTick(Methods method, String tick) {
        switch (method) {
            case WR_TIMER_TICK:
                try {
                    client.wrTimerTick(tick);
                    Logger.debug("updateTimerTick sent to " + nickname);
                } catch (RemoteException e) {
                    connectionError();
                }
                break;
            case GAME_TIMER_TICK:
                try {
                    client.gameTimerTick(tick);
                    Logger.debug("updateTimerTick sent to " + nickname);
                } catch (RemoteException e) {
                    connectionError();
                }
                break;
            default:
                break;
        }
    }

    @Override
    void updateWaitingPlayers(List<String> players) {
        Logger.debug("updateWaitingPlayers called");
        try {
            client.updateWaitingPlayers(players);
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    void setupGame() {
        Player player = this.gameController.getPlayer(uuid);

        ObjectiveCard privateObjectiveCard = player.getPrivateObjectiveCard();
        JsonObject privateObjectiveCardJSON = new JsonObject();
        privateObjectiveCardJSON.addProperty(JsonFields.NAME, privateObjectiveCard.getName());
        privateObjectiveCardJSON.addProperty(JsonFields.DESCRIPTION, privateObjectiveCard.getDescription());
        privateObjectiveCardJSON.addProperty(JsonFields.VICTORY_POINTS, privateObjectiveCard.getVictoryPoints());
        try {
            client.sendPrivateObjectiveCard(privateObjectiveCardJSON.toString());
        } catch (RemoteException e) {
            connectionError();
        }

        List<WindowPattern> windowPatterns = player.getWindowPatternList();
        List<String> windowPatternsJSON = new ArrayList<>();
        for (WindowPattern wp : windowPatterns) {
            windowPatternsJSON.add(createWindowPatternJSON(wp).toString());
        }
        try {
            client.sendSelectableWindowPatterns(windowPatternsJSON);
        } catch (RemoteException e) {
            connectionError();
        }

        Logger.println("A game has started for " + nickname);

    }

    @Override
    void fullUpdate() {

    }

    @Override
    void sendProbe() {
        try {
            client.probe();
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    void showDisconnectedUserMessage() {
        Logger.println(nickname + " was disconnected");
        Thread.currentThread().interrupt();
    }

    private void connectionError() {
        Logger.connectionLost(nickname);
        this.onUserDisconnection();
    }

}
