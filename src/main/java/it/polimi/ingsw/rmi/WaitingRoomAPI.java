package it.polimi.ingsw.rmi;

import it.polimi.ingsw.server.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observer;
import java.util.UUID;

public interface WaitingRoomAPI extends Remote {

    // WaitingRoom
    UUID addPlayer(String nickname) throws RemoteException;
    List<Player> getWaitingPlayers() throws RemoteException;
    void registerTimerForWaitingRoom(Observer observer) throws RemoteException;
}
