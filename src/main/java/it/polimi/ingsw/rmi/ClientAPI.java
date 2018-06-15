package it.polimi.ingsw.rmi;

import it.polimi.ingsw.model.game.*;
import java.rmi.*;
import java.util.*;


public interface ClientAPI extends Remote {

    void wrTimerTick(int tick) throws RemoteException;
    void updateWaitingPlayers(List<String> players) throws RemoteException;

}
