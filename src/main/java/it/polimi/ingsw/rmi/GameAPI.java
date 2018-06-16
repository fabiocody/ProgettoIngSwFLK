package it.polimi.ingsw.rmi;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.*;
import it.polimi.ingsw.model.game.DieAlreadyPlacedException;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.RoundTrack;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public interface GameAPI extends Remote {

    // Game
    List<String> getCurrentPlayers() throws RemoteException;
    void nextTurn() throws RemoteException;
    Map<String, Integer> getFinalScores() throws RemoteException;
    List<ObjectiveCard> getPublicObjectiveCards() throws RemoteException;
    List<ToolCard> getToolCards() throws RemoteException;

    // Player
    Player getPlayer(UUID id) throws RemoteException;
    String getActivePlayer() throws RemoteException;
    void suspendPlayer(UUID id) throws RemoteException;
    void unsuspendPlayer(UUID id) throws RemoteException;
    List<String> getSuspendedPlayers() throws RemoteException;
    int getFavorTokensOf(String nickname) throws RemoteException;
    WindowPattern getWindowPatternOf(String nickname) throws RemoteException;
    void choosePattern(UUID id, int patternIndex) throws RemoteException;

    // RoundTrack
    int getCurrentRound() throws RemoteException;
    List<Die> getRoundTrackDice() throws RemoteException;
    RoundTrack getRoundTrack() throws RemoteException;

    // Dice
    List<Die> getDraftPool() throws RemoteException;

    // Moves
    void placeDie(UUID playerID, int draftPoolIndex, int x, int y) throws RemoteException, InvalidPlacementException, DieAlreadyPlacedException;
    void useToolCard(UUID id, int toolCardsIndex, JsonObject data) throws RemoteException, InvalidEffectResultException, InvalidEffectArgumentException;
    JsonObject requiredData(int toolCardsIndex) throws RemoteException;
    
}
