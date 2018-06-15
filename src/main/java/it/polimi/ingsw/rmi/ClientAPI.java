package it.polimi.ingsw.rmi;

import it.polimi.ingsw.util.Methods;

import java.rmi.*;
import java.util.*;


public interface ClientAPI extends Remote {

    void probe() throws RemoteException;

    void wrTimerTick(String tick) throws RemoteException;
    void updateWaitingPlayers(List<String> players) throws RemoteException;

    void gameTimerTick(String tick) throws RemoteException;
    void sendPrivateObjectiveCard(String card) throws RemoteException;
    void sendSelectableWindowPatterns(List<String> cards) throws RemoteException;

    void updatePlayersList(List<String> players) throws RemoteException;
    void updateToolCards(List<String> cards) throws RemoteException;
    void sendPublicObjectiveCards(List<String> cards) throws RemoteException;
    void updateWindowPatterns(List<String> cards) throws RemoteException;
    void updateFavorTokens() throws RemoteException;
    void updateDraftPool() throws RemoteException;
    void updateRoundTrack() throws RemoteException;
    void turnManagement() throws RemoteException;
    void updateFinalScores() throws RemoteException;
    void setupGame() throws RemoteException;
    void fullUpdate() throws RemoteException;

}
