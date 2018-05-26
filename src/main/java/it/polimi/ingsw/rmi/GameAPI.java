package it.polimi.ingsw.rmi;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.*;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.model.toolcards.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public interface GameAPI extends Remote {

    // Game
    List<String> getCurrentPlayers() throws RemoteException;
    void nextTurn() throws RemoteException;
    void subscribeToTurnManagerTimer(Observer observer) throws RemoteException;
    Map<String, Integer> getFinalScores() throws RemoteException;
    List<ObjectiveCard> getPublicObjectiveCards() throws RemoteException;
    List<ToolCard> getToolCards() throws RemoteException;

    // Player
    Player getPlayer(UUID id) throws RemoteException;
    String getActivePlayer() throws RemoteException;
    int getFavorTokensOf(String nickname) throws RemoteException;
    WindowPattern getWindowPatternOf(String nickname) throws RemoteException;
    void choosePattern(UUID id, int patternIndex) throws RemoteException;

    // RoundTrack
    int getCurrentRound() throws RemoteException;
    List<Die> getRoundTrackDice() throws RemoteException;

    // Dice
    List<Die> getDraftPool() throws RemoteException;

    // Moves
    void placeDie(UUID playerID, int draftPoolIndex, int x, int y) throws RemoteException;
    void useToolCard(int toolCardsIndex, JsonObject data) throws RemoteException, InvalidEffectResultException, InvalidEffectArgumentException;
    
}
