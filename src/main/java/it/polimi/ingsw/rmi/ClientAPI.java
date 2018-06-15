package it.polimi.ingsw.rmi;

import java.rmi.*;
import java.util.*;


public interface ClientAPI extends Remote {

    void probe() throws RemoteException;

    void wrTimerTick(String tick) throws RemoteException;
    void updateWaitingPlayers(List<String> players) throws RemoteException;

    void gameTimerTick(String tick) throws RemoteException;
    void sendPrivateObjectiveCard(String card) throws RemoteException;
    void sendSelectableWindowPatterns(List<String> cards) throws RemoteException;

}
