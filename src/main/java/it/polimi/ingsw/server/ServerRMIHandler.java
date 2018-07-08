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

    /**
     * This method is the constructor that sets the client
     * @param client the client
     */
    private ServerRMIHandler(ClientAPI client) {
        System.setProperty("java.rmi.game.useCodebaseOnly", String.valueOf(false));
        this.client = client;
        if (this.client != null) WaitingRoomController.getInstance().addNetwork(this);
    }

    ServerRMIHandler() {
        this(null);
    }

    /**
     * This method is used to connect to the server
     *
     * @param client the client that wants to connect
     * @return the server endpoint used later
     */
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

    /**
     * This method is used to check if the server is still connected
     */
    @Override
    public void probe() {
        Logger.debug(getNickname() + " has sent a probe message");
        setProbed();
    }

    /**
     * @param nickname the nickname of the player logging in
     * @return a random UUID if the user has been added, null if login has failed
     */
    @Override
    public UUID addPlayer(String nickname) {
        Logger.debug(nickname + " is attempting to log in");
        setUUID(null);
        setNickname(null);
        try {
            setUUID(WaitingRoomController.getInstance().addPlayer(nickname));
            setNickname(nickname);
            Logger.log(nickname + " logged in successfully (" + getUUID() + ")");
            startProbeThread();
        } catch (LoginFailedException e) {
            Logger.error(nickname + " log in failed");
        } catch (NicknameAlreadyUsedInGameException e) {
            Logger.log("Welcome back " + nickname);
            setGameController(e.getController());
            getGameController().addNetwork(this);
            setNickname(nickname);
            Player player = getGameController().getGame().getPlayer(nickname);
            setUUID(player.getId());
            getGameController().unsuspendPlayer(getUUID());
            startProbeThread();
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
        return getUUID();
    }

    /**
     * @param patternIndex the index of the pattern chosen by the player
     * @return true if the index is valid and the pattern has been chosen, false otherwise
     */
    @Override
    public boolean choosePattern(int patternIndex) {
        try {
            getGameController().choosePattern(getUUID(), patternIndex);
            Logger.log(getNickname() + " has chosen pattern " + patternIndex);
            return true;
        } catch (IndexOutOfBoundsException e) {
            Logger.log("Pattern choosing for " + getNickname() + " unsuccessful");
            return false;
        }
    }

    /**
     * @param draftPoolIndex the index of the die in the Draft Pool to be placed
     * @param x the column in which to place the die
     * @param y the row in which to place the die
     * @return the serialized JSON describing the result of the placement
     */
    @Override
    public String placeDie(int draftPoolIndex, int x, int y) {
        JsonObject payload = new JsonObject();
        try {
            getGameController().placeDie(getUUID(), draftPoolIndex, x, y);
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

    /**
     * @param cardIndex the index of the Tool Card you need data for
     * @return the data required by the specified Tool Card, JSON-serialized
     */
    @Override
    public String requiredData(int cardIndex) {
        JsonObject payload = getGameController().requiredData(cardIndex, getUUID());
        Player player = getGameController().getPlayer(getUUID());
        if ((player.getFavorTokens()<2 && getGameController().getToolCards().get(cardIndex).isUsed()) ||
                (player.getFavorTokens()<1 && !getGameController().getToolCards().get(cardIndex).isUsed())){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS, Constants.INDEX_CONSTANT);
        }
        if (payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX) && getGameController().getRoundTrack()
                .getFlattenedDice().isEmpty()){
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.ROUND_TRACK);
        }
        if ((payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) &&
                (!(payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X))) && (player.isDiePlacedInThisTurn()) &&
                (!payload.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.SECOND_DIE_PLACEMENT))) {
            payload.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, JsonFields.DIE);
        }
        return payload.toString();
    }

    /**
     * @param cardIndex the index of the Tool Card you want to use
     * @param requiredDataString the JSON-serialized data required to use the Tool Card
     * @return the serialized JSON describing the result of the Tool Card usage
     */
    @Override
    public String useToolCard(int cardIndex, String requiredDataString) {
        JsonObject requiredData = getJsonParser().parse(requiredDataString).getAsJsonObject();
        requiredData.addProperty(JsonFields.PLAYER_ID, getUUID().toString());
        JsonObject payload = new JsonObject();
        try {
            getGameController().useToolCard(getUUID(), cardIndex, requiredData);
            payload.addProperty(JsonFields.RESULT, true);
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            Logger.printStackTraceConditionally(e);
            Logger.log(getNickname() + " usage of tool card was refused");
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE,e.getMessage());
        }
        return payload.toString();
    }

    /**
     * @param cardIndex the index of the Tool Card
     */
    @Override
    public void cancelToolCardUsage(int cardIndex) {
        getGameController().cancelToolCardUsage(getUUID(), cardIndex);
    }

    /**
     * This method is used to pass the turn
     */
    @Override
    public void nextTurn() {
        getGameController().nextTurn();
        Logger.log(getNickname() + " has ended his turn");
    }

    /**
     * This method is used to create an update for the client
     *
     * @param payload containing the information for the update
     */
    private void clientUpdate(String payload) {
        try {
            if (client != null)
                client.update(payload);
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    /**
     * @return JsonObject containing the information necessary for a players list update
     */
    @Override
    JsonObject updatePlayersList() {
        JsonObject payload = super.updatePlayersList();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a tool card update
     */
    @Override
    JsonObject updateToolCards() {
        JsonObject payload = super.updateToolCards();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information of the public objective cards
     */
    @Override
    JsonObject sendPublicObjectiveCards() {
        JsonObject payload = super.sendPublicObjectiveCards();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a window pattern update
     */
    @Override
    JsonObject updateWindowPatterns() {
        JsonObject payload = super.updateWindowPatterns();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a favor token update
     */
    @Override
    JsonObject updateFavorTokens() {
        JsonObject payload = super.updateFavorTokens();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a draft pool update
     */
    @Override
    JsonObject updateDraftPool() {
        JsonObject payload = super.updateDraftPool();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a round track update
     */
    @Override
    JsonObject updateRoundTrack() {
        JsonObject payload = super.updateRoundTrack();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a turn manager update
     */
    @Override
    JsonObject turnManagement() {
        JsonObject payload = super.turnManagement();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for a final score update
     */
    @Override
    JsonObject updateFinalScores() {
        JsonObject payload = super.updateFinalScores();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @param method can be WR_TIMER_TICK or GAME_TIMER_TICK depending on what timer we want to update
     * @param tick the remaining time
     * @return JsonObject containing the information necessary for a timer update
     */
    @Override
    JsonObject updateTimerTick(Methods method, String tick) {
        JsonObject payload = super.updateTimerTick(method, tick);
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for waiting players update
     */
    @Override
    JsonObject updateWaitingPlayers() {
        JsonObject payload = super.updateWaitingPlayers();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * @return JsonObject containing the information necessary for the game setup
     */
    @Override
    JsonObject setupGame() {
        JsonObject payload = super.setupGame();
        clientUpdate(payload.toString());
        return payload;
    }

    /**
     * This method is used to send a probe to check if the client is still connected
     */
    @Override
    void sendProbe() {
        try {
            client.probe();
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    /**
     * This method is used to show the disconnected user message
     */
    @Override
    void showDisconnectedUserMessage() {
        Logger.log(getNickname() + " was disconnected");
        Thread.currentThread().interrupt();
    }

    /**
     * This method is used to close the connection
     */
    @Override
    void close() {
        client = null;
    }

    /**
     * @param e the thrown exception
     */
    private void connectionError(Throwable e) {
        Logger.connectionLost(getNickname());
        Logger.printStackTraceConditionally(e);
        this.onUserDisconnection();
        this.close();
    }

}
