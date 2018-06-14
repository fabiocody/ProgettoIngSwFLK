package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerAPI extends Remote, WaitingRoomAPI, GameAPI {
    ServerAPI connect() throws RemoteException;
}
