package it.polimi.ingsw.shared.rmi;

import com.google.gson.JsonObject;
import java.rmi.*;
import java.util.*;


public interface ServerAPI extends Remote {

    static String getServerRMIName(String host, int port) throws RemoteException {
        return "//" + host + ":" + port + "/Server";
    }

    ServerAPI connect(ClientAPI client) throws RemoteException;
    void probe() throws RemoteException;

    UUID addPlayer(String nickname) throws RemoteException;
    void choosePattern(int patternIndex) throws RemoteException;
    boolean placeDie(int draftPoolIndex, int x, int y) throws RemoteException;
    void nextTurn() throws RemoteException;
    JsonObject requiredData(int cardIndex) throws RemoteException;
    boolean useToolCard(int cardIndex, JsonObject requiredData) throws RemoteException;

}
