package it.polimi.ingsw.server;

import it.polimi.ingsw.rmi.WaitingRoomAPI;

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
    public void registerTimerForWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().getTimer().addObserver(observer);
    }
}
