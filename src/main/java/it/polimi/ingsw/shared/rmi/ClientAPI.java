package it.polimi.ingsw.shared.rmi;

import java.rmi.*;


public interface ClientAPI extends Remote {

    void probe() throws RemoteException;
    void update(String jsonString) throws RemoteException;

}
