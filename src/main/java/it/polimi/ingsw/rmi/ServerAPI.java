package it.polimi.ingsw.rmi;

import java.rmi.*;
import java.util.*;


public interface ServerAPI extends Remote {

    ServerAPI connect(ClientAPI client) throws RemoteException;

    UUID addPlayer(String nickname) throws RemoteException;

    void nextTurn() throws RemoteException;
}
