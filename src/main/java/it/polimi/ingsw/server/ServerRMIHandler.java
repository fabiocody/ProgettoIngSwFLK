package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.WindowPattern;
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
            Logger.println(nickname + " logged in successfully");
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
    public void nextTurn() throws RemoteException {

    }


    @Override
    void updatePlayersList() {

    }

    @Override
    void updateToolCards() {

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
    void updateTimerTick(Methods method, int tick) {
        switch (method) {
            case WR_TIMER_TICK:
                try {
                    client.wrTimerTick(tick);
                    Logger.debug("updateTimerTick sent to " + nickname);
                } catch (RemoteException e) {
                    Logger.error("Connection error: " + e.getMessage());
                }
                break;
            case GAME_TIMER_TICK:
                try {
                    client.gameTimerTick(tick);
                    Logger.debug("updateTimerTick sent to " + nickname);
                } catch (RemoteException e) {
                    Logger.error("Connection error: " + e.getMessage());
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
            Logger.error("Connection error: " + e.getMessage());
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
            Logger.error("Connection error: " + e.getMessage());
        }

        List<WindowPattern> windowPatterns = player.getWindowPatternList();
        List<String> windowPatternsJSON = new ArrayList<>();
        for (WindowPattern wp : windowPatterns) {
            windowPatternsJSON.add(createWindowPatternJSON(wp).toString());
        }
        try {
            client.sendSelectableWindowPatterns(windowPatternsJSON);
        } catch (RemoteException e) {
            Logger.error("Connection error: " + e.getMessage());
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
            Logger.error("Connection error: " + e.getMessage());
        }
    }

    @Override
    void notifyDisconnectedUser() {
        Logger.println(nickname + " was disconnected");
        WaitingRoomController.getInstance().removePlayer(this.nickname);
        if (gameController != null) {
            gameController.suspendPlayer(this.uuid);
        }
        Thread.currentThread().interrupt();
    }

}
