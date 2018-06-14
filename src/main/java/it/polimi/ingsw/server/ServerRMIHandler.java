package it.polimi.ingsw.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.toolcards.InvalidEffectArgumentException;
import it.polimi.ingsw.model.toolcards.InvalidEffectResultException;
import it.polimi.ingsw.model.toolcards.ToolCard;
import it.polimi.ingsw.rmi.ServerAPI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.UUID;


public class ServerRMIHandler extends UnicastRemoteObject implements ServerAPI {

    ServerRMIHandler() throws RemoteException {
    }

    @Override
    public List<String> getCurrentPlayers() {
        return null;
    }

    @Override
    public void nextTurn() throws RemoteException {

    }

    @Override
    public void subscribeToTurnManagerTimer(Observer observer) throws RemoteException {

    }

    @Override
    public void unsubscribeFromTurnManagerTimer(Observer observer) throws RemoteException {

    }

    @Override
    public Map<String, Integer> getFinalScores() throws RemoteException {
        return null;
    }

    @Override
    public List<ObjectiveCard> getPublicObjectiveCards() throws RemoteException {
        return null;
    }

    @Override
    public List<ToolCard> getToolCards() throws RemoteException {
        return null;
    }

    @Override
    public Player getPlayer(UUID id) throws RemoteException {
        return null;
    }

    @Override
    public String getActivePlayer() throws RemoteException {
        return null;
    }

    @Override
    public void suspendPlayer(UUID id) throws RemoteException {

    }

    @Override
    public void unsuspendPlayer(UUID id) throws RemoteException {

    }

    @Override
    public List<String> getSuspendedPlayers() throws RemoteException {
        return null;
    }

    @Override
    public int getFavorTokensOf(String nickname) throws RemoteException {
        return 0;
    }

    @Override
    public WindowPattern getWindowPatternOf(String nickname) throws RemoteException {
        return null;
    }

    @Override
    public void choosePattern(UUID id, int patternIndex) throws RemoteException {

    }

    @Override
    public boolean arePlayersReady() throws RemoteException {
        return false;
    }

    @Override
    public int getCurrentRound() throws RemoteException {
        return 0;
    }

    @Override
    public List<Die> getRoundTrackDice() throws RemoteException {
        return null;
    }

    @Override
    public RoundTrack getRoundTrack() throws RemoteException {
        return null;
    }

    @Override
    public List<Die> getDraftPool() throws RemoteException {
        return null;
    }

    @Override
    public void placeDie(UUID playerID, int draftPoolIndex, int x, int y) throws RemoteException, InvalidPlacementException, DieAlreadyPlacedException {

    }

    @Override
    public void useToolCard(UUID id, int toolCardsIndex, JsonObject data) throws RemoteException, InvalidEffectResultException, InvalidEffectArgumentException {

    }

    @Override
    public JsonObject requiredData(int toolCardsIndex) throws RemoteException {
        return null;
    }

    @Override
    public UUID addPlayer(String nickname) throws RemoteException, LoginFailedException, NicknameAlreadyUsedInGameException {
        return null;
    }

    @Override
    public void removePlayer(String nickname) throws RemoteException {

    }

    @Override
    public List<Player> getWaitingPlayers() throws RemoteException {
        return null;
    }

    @Override
    public ServerAPI connect() throws RemoteException {
        return new ServerRMIHandler();
    }
}
