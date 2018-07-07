package it.polimi.ingsw.shared.rmi;

import java.rmi.*;
import java.util.*;


/**
 * This is the server interface used for RMI
 */
public interface ServerAPI extends Remote {

    /**
     * This method is used to connect to the server
     *
     * @param client the client that wants to connect
     * @return the server endpoint used later
     * @throws RemoteException thrown when there are connection problems
     */
    ServerAPI connect(ClientAPI client) throws RemoteException;

    /**
     * This method is used to check if the server is still connected
     *
     * @throws RemoteException thrown when there are connection problems
     */
    void probe() throws RemoteException;

    /**
     * @param nickname the nickname of the player logging in
     * @return a random UUID if the user has been added, null if login has failed
     * @throws RemoteException thrown when there are connection problems
     */
    UUID addPlayer(String nickname) throws RemoteException;

    /**
     * @param patternIndex the index of the pattern chosen by the player
     * @return true if the index is valid and the pattern has been chosen, false otherwise
     * @throws RemoteException thrown when there are connection problems
     */
    boolean choosePattern(int patternIndex) throws RemoteException;

    /**
     * @param draftPoolIndex the index of die the in the Draft Pool to be placed
     * @param x the column in which to place the die
     * @param y the row in which to place the die
     * @return the serialized JSON describing the result of the placement
     * @throws RemoteException thrown when there are connection problems
     */
    String placeDie (int draftPoolIndex, int x, int y) throws RemoteException;

    /**
     * This method toggles turn ending
     *
     * @throws RemoteException thrown when there are connection problems
     */
    void nextTurn() throws RemoteException;

    /**
     * @param cardIndex the index of the Tool Card you need data for
     * @return the data required by the specified Tool Card, JSON-serialized
     * @throws RemoteException thrown when there are connection problems
     */
    String requiredData(int cardIndex) throws RemoteException;

    /**
     * @param cardIndex the index of the Tool Card you want to use
     * @param requiredDataString the JSON-serialized data required to use the Tool Card
     * @return the serialized JSON describing the result of the Tool Card usage
     * @throws RemoteException thrown when there are connection problems
     */
    String useToolCard(int cardIndex, String requiredDataString) throws RemoteException;

    /**
     * @param cardIndex the index of the Tool Card
     * @throws RemoteException thrown when there are connection problems
     */
    void cancelToolCardUsage(int cardIndex) throws RemoteException;

}
