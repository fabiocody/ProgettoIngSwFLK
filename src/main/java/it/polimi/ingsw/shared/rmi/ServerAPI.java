package it.polimi.ingsw.shared.rmi;

import com.google.gson.JsonObject;
import java.rmi.*;
import java.util.*;


public interface ServerAPI extends Remote {

    ServerAPI connect(ClientAPI client) throws RemoteException;
    void probe() throws RemoteException;

    UUID addPlayer(String nickname) throws RemoteException;
    void choosePattern(int patternIndex) throws RemoteException;
    String placeDie (int draftPoolIndex, int x, int y) throws RemoteException;
    void nextTurn() throws RemoteException;
    String requiredData(int cardIndex) throws RemoteException;
    String useToolCard(int cardIndex, String requiredDataString) throws RemoteException;

}
