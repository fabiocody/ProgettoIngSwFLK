package it.polimi.ingsw.rmi;

import it.polimi.ingsw.util.Methods;

import java.rmi.*;
import java.util.*;


public interface ClientAPI extends Remote {

    void probe() throws RemoteException;
    void update(String jsonString) throws RemoteException;

}
