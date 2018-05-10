package it.polimi.ingsw.server;

import it.polimi.ingsw.rmi.WaitingRoomAPI;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

public class WaitingRoomEndPoint implements WaitingRoomAPI {

    @Override
    public boolean addPlayer(String nickname) {
        return WaitingRoom.getInstance().addPlayer(nickname);
    }

    @Override
    public List<String> getWaitingPlayers() {
        return new Vector<>(WaitingRoom.getInstance().getNicknames());
    }
}
