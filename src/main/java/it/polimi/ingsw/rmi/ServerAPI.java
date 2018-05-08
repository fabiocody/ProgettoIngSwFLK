package it.polimi.ingsw.rmi;

import it.polimi.ingsw.dice.*;
import it.polimi.ingsw.objectivecards.*;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.toolcards.*;
import java.util.*;


public interface ServerAPI {

    // Game
    List<String> getCurrentPlayers();
    void nextTurn();
    void registerTimerForWaitingRoom(Observer observer);
    void registerTimerForTurnManager(Observer observer);
    Map<String, Integer> getFinalScores();
    List<ObjectiveCard> getPublicObjectiveCards();
    List<ToolCard> getToolCards();

    // Player
    Player getYourOwnPlayerObject(String nickname);
    int getFavorTokensOf(String nickname);
    WindowPattern getWindowPatternOf(String nickname);

    // RoundTrack
    int getCurrentRound();
    List<Die> getRoundTrackDice();

    // WaitingRoom
    boolean addPlayer(String nickname);
    List<String> getWaitingPlayers();

    // Dice
    List<Die> getDraftPool();

}
