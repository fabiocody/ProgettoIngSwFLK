package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observer;

public interface WaitingRoomAPI extends Remote {

    // WaitingRoom
    boolean addPlayer(String nickname) throws RemoteException;
    List<String> getWaitingPlayers() throws RemoteException;
    void registerTimerForWaitingRoom(Observer observer) throws RemoteException;
}
