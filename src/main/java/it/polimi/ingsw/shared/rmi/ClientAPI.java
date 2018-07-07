package it.polimi.ingsw.shared.rmi;

import java.rmi.*;


/**
 * This is the client interface used for RMI
 */
public interface ClientAPI extends Remote {

    /**
     * This method is used to check if the client is still connected
     *
     * @throws RemoteException thrown when there are connection problems
     */
    void probe() throws RemoteException;

    /**
     * This method is used to send an update to a client
     *
     * @param jsonString the payload sent to the client
     * @throws RemoteException thrown when there are connection problems
     */
    void update(String jsonString) throws RemoteException;

    /**
     * This method is used to rejoin an already started game
     *
     * @param privateObjectiveCard the serialized JSON representing a Private Objective Card
     * @throws RemoteException thrown when there are connection problems
     */
    void reconnect(String privateObjectiveCard) throws RemoteException;

}
