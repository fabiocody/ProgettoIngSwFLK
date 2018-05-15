package it.polimi.ingsw.server;

import it.polimi.ingsw.rmi.WaitingRoomAPI;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Observer;
import java.util.UUID;
import java.util.Vector;

public class WaitingRoomEndPoint implements WaitingRoomAPI {

    @Override
    public UUID addPlayer(String nickname) {
        return WaitingRoom.getInstance().addPlayer(nickname);
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
