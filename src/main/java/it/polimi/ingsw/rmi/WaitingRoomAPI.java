package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WaitingRoomAPI extends Remote {

    // WaitingRoom
    boolean addPlayer(String nickname) throws RemoteException;
    List<String> getWaitingPlayers() throws RemoteException;

}
