package it.polimi.ingsw.rmi;

import it.polimi.ingsw.server.*;
import java.rmi.*;
import java.util.*;


public interface WaitingRoomAPI extends Remote {

    // WaitingRoom
    UUID addPlayer(String nickname) throws RemoteException, LoginFailedException;
    void removePlayer(String nickname) throws RemoteException;
    List<Player> getWaitingPlayers() throws RemoteException;
    void subscribeToWaitingRoomTimer(Observer observer) throws RemoteException;
    void unsubscribeFromWaitingRoomTimer(Observer observer) throws RemoteException;
    void subscribeToWaitingRoom(Observer observer) throws RemoteException;
    void unsubscribeFromWaitingRoom(Observer observer) throws RemoteException;
}
