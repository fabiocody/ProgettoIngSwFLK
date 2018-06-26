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
        System.setProperty("java.rmi.server.useCodebaseOnly", String.valueOf(false));
        this.client = client;
        this.jsonParser = new JsonParser();
        if (this.client != null) WaitingRoomController.getInstance().addServerNetwork(this);
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
            if (Logger.isDebugActive()) e.printStackTrace();
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
            Logger.log(nickname + " logged in successfully (" + uuid + ")");
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            Logger.error(nickname + " log in failed");
        } catch (NicknameAlreadyUsedInGameException e) {
            Logger.log("Welcome back " + nickname);
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
            if (Logger.isDebugActive()) e.printStackTrace();
            return false;
        }
    }

    @Override
    public void nextTurn() {
        gameController.nextTurn();
        Logger.log(this.nickname + " has ended his turn");
    }

    @Override
    public String requiredData(int cardIndex) {
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
        return payload.toString();
    }

    @Override
    public boolean useToolCard(int cardIndex, String requiredDataString) {
        JsonObject requiredData = jsonParser.parse(requiredDataString).getAsJsonObject();
        requiredData.addProperty(JsonFields.PLAYER_ID, uuid.toString());
        try {
            this.gameController.useToolCard(uuid, cardIndex, requiredData);
            Logger.log(this.nickname + " used a tool card");
            return true;
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            if (Logger.isDebugActive()) e.printStackTrace();
            Logger.log(this.nickname + " usage of tool card was refused");
            return false;
        }
    }

    private void clientUpdate(String payload) {
        try {
            client.update(payload);
        } catch (RemoteException e) {
            connectionError(e);
        }
    }


    @Override
    JsonObject updatePlayersList() {
        JsonObject payload = super.updatePlayersList();
        Logger.debug("PAYLOAD " + payload.toString());
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
        Logger.log(nickname + " was disconnected");
        Thread.currentThread().interrupt();
    }

    private void connectionError(Throwable e) {
        Logger.connectionLost(nickname);
        if (Logger.isDebugActive())
            e.printStackTrace();
        this.onUserDisconnection();
    }

}
