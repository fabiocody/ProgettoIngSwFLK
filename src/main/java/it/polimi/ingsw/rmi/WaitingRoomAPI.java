package it.polimi.ingsw.rmi;

import it.polimi.ingsw.model.game.LoginFailedException;
import it.polimi.ingsw.model.game.NicknameAlreadyUsedInGameException;
import it.polimi.ingsw.model.game.Player;

import java.rmi.*;
import java.util.*;


/**
 * This is the shared interface that describes the methods that the client can call on the waiting room
 *
 * @author Team
 */
public interface WaitingRoomAPI extends Remote {

    /**
     * This method is used when a client wants to join the waiting room
     *
     * @param nickname the nickname inserted by the player
     * @return UUID the secret identifier used to uniquely authenticate the player
     * @throws RemoteException required by RMI
     * @throws LoginFailedException thrown when the player inserts a nickname that is already present on the sever
     * side
     */
    UUID addPlayer(String nickname) throws RemoteException, LoginFailedException, NicknameAlreadyUsedInGameException;

    /**
     * This method is used to remove a player from the waiting room
     *
     * @param nickname the nickname of the player that has to leave the waiting room
     * @throws RemoteException required by RMI
     */
    void removePlayer(String nickname) throws RemoteException;

    /**
     * @return a list of the players currently logged in the waiting room
     * @throws RemoteException required by RMI
     */
    List<Player> getWaitingPlayers() throws RemoteException;

    /**
     * This method is used to subscribe a player to the waiting room timer, so that the client will receive regular
     * updates
     *
     * @param observer the client that wants to be subscribed
     * @throws RemoteException required by RMI
     */
    //void subscribeToWaitingRoomTimer(Observer observer) throws RemoteException;

    /**
     * This method is used to unsubscribe a player from the waiting room timer
     *
     * @param observer the client that no longer wants to be subscribed
     * @throws RemoteException required by RMI
     */
    //void unsubscribeFromWaitingRoomTimer(Observer observer) throws RemoteException;

    /**
     * This method is used to subscribe a player to the waiting room, so that the client will receive regular
     * updates
     *
     * @param observer the client that wants to be subscribed
     * @throws RemoteException required by RMI
     */
    //void subscribeToWaitingRoom(Observer observer) throws RemoteException;

    /**
     * This method is used to unsubscribe a player from the waiting room timer
     *
     * @param observer the client that no longer wants to be subscribed
     * @throws RemoteException required by RMI
     */
    //void unsubscribeFromWaitingRoom(Observer observer) throws RemoteException;
}
