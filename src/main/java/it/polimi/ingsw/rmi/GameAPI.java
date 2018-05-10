package it.polimi.ingsw.rmi;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.*;
import it.polimi.ingsw.objectivecards.*;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.toolcards.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public interface GameAPI extends Remote {

    // Game
    List<String> getCurrentPlayers() throws RemoteException;
    void nextTurn() throws RemoteException;
    void registerTimerForTurnManager(Observer observer) throws RemoteException;
    Map<String, Integer> getFinalScores() throws RemoteException;
    List<ObjectiveCard> getPublicObjectiveCards() throws RemoteException;
    List<ToolCard> getToolCards() throws RemoteException;

    // Player
    Player getYourOwnPlayerObject(String nickname) throws RemoteException;
    int getFavorTokensOf(String nickname) throws RemoteException;
    WindowPattern getWindowPatternOf(String nickname) throws RemoteException;

    // RoundTrack
    int getCurrentRound() throws RemoteException;
    List<Die> getRoundTrackDice() throws RemoteException;

    // Dice
    List<Die> getDraftPool() throws RemoteException;

    // Moves
    void placeDie(int draftPoolIndex, int x, int y) throws RemoteException;
    void useToolCard(int toolCardsIndex, JsonObject data) throws RemoteException;

}
