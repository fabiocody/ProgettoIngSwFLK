package it.polimi.ingsw.server;

import it.polimi.ingsw.rmi.WaitingRoomAPI;
import java.util.*;


public class WaitingRoomEndPoint implements WaitingRoomAPI {

    private static WaitingRoomEndPoint instance;

    private WaitingRoomEndPoint() {}

    public static WaitingRoomEndPoint getInstance() {
        if (instance == null)
            instance = new WaitingRoomEndPoint();
        return instance;
    }

    @Override
    public UUID addPlayer(String nickname) throws LoginFailedException, NicknameAlreadyUsedInGameException {
        return WaitingRoom.getInstance().addPlayer(nickname);
    }

    @Override
    public void removePlayer(String nickname){
        WaitingRoom.getInstance().removePlayer(nickname);
    }

    @Override
    public List<Player> getWaitingPlayers() {
        return new Vector<>(WaitingRoom.getInstance().getWaitingPlayers());
    }

    @Override
    public void subscribeToWaitingRoomTimer(Observer observer) {
        WaitingRoom.getInstance().getTimer().addObserver(observer);
    }

    @Override
    public void unsubscribeFromWaitingRoomTimer(Observer observer) {
        WaitingRoom.getInstance().getTimer().deleteObserver(observer);
    }

    @Override
    public void subscribeToWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().addObserver(observer);
    }

    @Override
    public void unsubscribeFromWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().deleteObserver(observer);
    }

}
