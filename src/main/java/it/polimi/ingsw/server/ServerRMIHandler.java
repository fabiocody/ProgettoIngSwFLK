package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.util.*;

import java.rmi.RemoteException;
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
            return (ServerAPI) UnicastRemoteObject.exportObject(new ServerRMIHandler(client), 0);
        } catch (RemoteException e) {
            Logger.error("Cannot export new ServerAPI");
            return null;
        }
    }

    @Override
    public UUID addPlayer(String nickname) {
        Logger.debug(nickname + " is attempting to log in");
        UUID uuid = null;
        try {
            uuid = WaitingRoomController.getInstance().addPlayer(nickname);
            this.nickname = nickname;
            Logger.println(nickname + " logged in successfully");
        } catch (LoginFailedException e) {
            Logger.error(nickname + " log in failed");
        } catch (NicknameAlreadyUsedInGameException e) {
            Logger.println("Welcome back " + nickname);
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
                    Logger.error("Connection error");
                }
                break;
                // TODO GAME_TIMER_TICK
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
            Logger.error("Connection error");
        }
    }

    @Override
    void setupGame() {

    }

    @Override
    void fullUpdate() {

    }
}
