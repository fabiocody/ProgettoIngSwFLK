package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.shared.rmi.*;
import it.polimi.ingsw.shared.util.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class ServerRMIHandler extends ServerNetwork implements ServerAPI {

    private ClientAPI client;
    private JsonParser jsonParser;

    private ServerRMIHandler(ClientAPI client) {
        System.setProperty("java.rmi.game.useCodebaseOnly", String.valueOf(false));
        this.client = client;
        this.jsonParser = new JsonParser();
        if (this.client != null) WaitingRoomController.getInstance().addNetwork(this);
    }

    ServerRMIHandler() {
        this(null);
    }

    @Override
    public ServerAPI connect(ClientAPI client) {
        Logger.log("User connected via RMI");
        try {
            ServerNetwork remoteServer = new ServerRMIHandler(client);
            SagradaServer.getInstance().addObserver(remoteServer);
            return (ServerAPI) UnicastRemoteObject.exportObject((ServerAPI) remoteServer, 0);
        } catch (RemoteException e) {
            Logger.error("Cannot export new ServerAPI");
            Logger.printStackTraceConditionally(e);
            return null;
        }
    }

    @Override
    public void probe() {
        Logger.debug(getNickname() + " has sent a probe message");
        setProbed(true);
    }

    @Override
    public UUID addPlayer(String nickname) {
        Logger.debug(nickname + " is attempting to log in");
        setUuid(null);
        setNickname(null);
        try {
            setUuid(WaitingRoomController.getInstance().addPlayer(nickname));
            setNickname(nickname);
            Logger.log(nickname + " logged in successfully (" + getUuid() + ")");
            setProbeThread(new Thread(this::probeCheck));
            getProbeThread().start();
        } catch (LoginFailedException e) {
            Logger.error(nickname + " log in failed");
        } catch (NicknameAlreadyUsedInGameException e) {
            Logger.log("Welcome back " + nickname);
            setGameController(e.getController());
            getGameController().addNetwork(this);
            setNickname(nickname);
            Player player = getGameController().getGame().getPlayer(nickname);
            setUuid(player.getId());
            getGameController().unsuspendPlayer(getUuid());
            setProbeThread(new Thread(this::probeCheck));
            getProbeThread().start();
            JsonObject privateObjectiveCard = createObjectiveCardJson(player.getPrivateObjectiveCard());
            try {
                client.reconnect(privateObjectiveCard.toString());
            } catch (RemoteException e1) {
                connectionError(e1);
            }
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    fullUpdate();
                }
            }, 500);
        }
        return getUuid();
    }

    @Override
    public void choosePattern(int patternIndex) {
        getGameController().choosePattern(getUuid(), patternIndex);
        Logger.log(getNickname() + " has chosen pattern " + patternIndex);
    }

    @Override
    public String placeDie(int draftPoolIndex, int x, int y) {
        JsonObject payload = new JsonObject();
        try {
            getGameController().placeDie(getUuid(), draftPoolIndex, x, y);
            Logger.log(getNickname() + " placed a die");
            payload.addProperty(JsonFields.RESULT, true);
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            Logger.log(getNickname() + "'s die placement attempt was refused");
            Logger.printStackTraceConditionally(e);
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE,e.getMessage());
        }
        return payload.toString();
    }

    @Override
    public String requiredData(int cardIndex) {
        JsonObject payload = getGameController().requiredData(cardIndex, getUuid());
        Player player = getGameController().getPlayer(getUuid());
        if ((player.getFavorTokens()<2 && getGameController().getToolCards().get(cardIndex).isUsed()) ||
                (player.getFavorTokens()<1 && !getGameController().getToolCards().get(cardIndex).isUsed())){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS, Constants.INDEX_CONSTANT);
        }
        if (payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX) && getGameController().getRoundTrack().getFlattenedDice().isEmpty()){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.ROUND_TRACK);
        }
        if ((payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) &&
                (!(payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X))) && (player.isDiePlacedInThisTurn()) && (!payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.SECOND_DIE_PLACEMENT))) {
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.DIE);
        }
        return payload.toString();
    }

    @Override
    public String useToolCard(int cardIndex, String requiredDataString) {
        JsonObject requiredData = jsonParser.parse(requiredDataString).getAsJsonObject();
        requiredData.addProperty(JsonFields.PLAYER_ID, getUuid().toString());
        JsonObject payload = new JsonObject();
        try {
            getGameController().useToolCard(getUuid(), cardIndex, requiredData);
            Logger.log(getNickname() + " used a tool card");
            payload.addProperty(JsonFields.RESULT, true);
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            Logger.printStackTraceConditionally(e);
            Logger.log(getNickname() + " usage of tool card was refused");
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE,e.getMessage());
        }
        return payload.toString();
    }

    @Override
    public void nextTurn() {
        getGameController().nextTurn();
        Logger.log(getNickname() + " has ended his turn");
    }

    private void clientUpdate(String payload) {
        try {
            if (client != null)
                client.update(payload);
        } catch (RemoteException e) {
            connectionError(e);
        }
    }


    @Override
    JsonObject updatePlayersList() {
        JsonObject payload = super.updatePlayersList();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateToolCards() {
        JsonObject payload = super.updateToolCards();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject sendPublicObjectiveCards() {
        JsonObject payload = super.sendPublicObjectiveCards();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateWindowPatterns() {
        JsonObject payload = super.updateWindowPatterns();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateFavorTokens() {
        JsonObject payload = super.updateFavorTokens();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateDraftPool() {
        JsonObject payload = super.updateDraftPool();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateRoundTrack() {
        JsonObject payload = super.updateRoundTrack();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject turnManagement() {
        JsonObject payload = super.turnManagement();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateFinalScores() {
        JsonObject payload = super.updateFinalScores();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateTimerTick(Methods method, String tick) {
        JsonObject payload = super.updateTimerTick(method, tick);
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateWaitingPlayers() {
        JsonObject payload = super.updateWaitingPlayers();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    JsonObject setupGame() {
        JsonObject payload = super.setupGame();
        clientUpdate(payload.toString());
        return payload;
    }

    @Override
    void sendProbe() {
        try {
            client.probe();
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    @Override
    void showDisconnectedUserMessage() {
        Logger.log(getNickname() + " was disconnected");
        Thread.currentThread().interrupt();
    }

    @Override
    void close() {
        client = null;
    }

    private void connectionError(Throwable e) {
        Logger.connectionLost(getNickname());
        Logger.printStackTraceConditionally(e);
        this.onUserDisconnection();
        this.close();
    }

}
