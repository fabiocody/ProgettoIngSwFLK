package it.polimi.ingsw.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.rmi.GameAPI;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.Constants;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;


public class GameEndPoint implements GameAPI {

    private Game game;

    GameEndPoint(Game game) {
        this.game = game;
    }

    public List<String> getCurrentPlayers() {
        return this.game.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    @Override
    public void nextTurn() {
        this.game.getTurnManager().nextTurn();
    }

    @Override
    public void subscribeToTurnManagerTimer(Observer observer) {
        this.game.getTurnManager().getTimer().addObserver(observer);
    }

    @Override
    public Map<String, Integer> getFinalScores() {
        return this.game.getFinalScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getNickname(),
                        Map.Entry::getValue
                ));
    }

    @Override
    public List<ObjectiveCard> getPublicObjectiveCards() {
        return this.game.getPublicObjectiveCards();
    }

    @Override
    public List<ToolCard> getToolCards() {
        return this.game.getToolCards();
    }

    @Override
    public Player getPlayer(UUID id) {
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object for uuid " + id);
    }

    @Override
    public String getActivePlayer(){
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(Player::isActive)
                .findFirst();
        if (result.isPresent()) return result.get().getNickname();
        else throw new NoSuchElementException("No Active Player found");
    }

    @Override
    public int getFavorTokensOf(String nickname) {
        Optional<Integer> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getFavorTokens)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve favor tokens because no Player object with specified nickname has been found");
    }

    @Override
    public WindowPattern getWindowPatternOf(String nickname) {
        Optional<WindowPattern> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getWindowPattern)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve WindowPattern because no Player object with specified nickname has been found");
    }

    @Override
    public void choosePattern(UUID id, int patternIndex){
        getPlayer(id).chooseWindowPattern(patternIndex);
    }

    @Override
    public boolean arePlayersReady() {
        return this.game.arePlayersReady();
    }

    @Override
    public int getCurrentRound() {
        return this.game.getRoundTrack().getCurrentRound();
    }

    @Override
    public List<Die> getRoundTrackDice() {
        return new Vector<>(this.game.getRoundTrack().getDice());
    }

    @Override
    public List<Die> getDraftPool() {
        return this.game.getDiceGenerator().getDraftPool();
    }


    @Override
    public void placeDie(UUID playerID, int draftPoolIndex, int x, int y) throws RemoteException, InvalidPlacementException, DieAlreadyPlacedException{
        WindowPattern wp = getPlayer(playerID).getWindowPattern();
        Die d = this.game.getDiceGenerator().getDraftPool().get(draftPoolIndex);
        if(getPlayer(playerID).isDiePlacedInThisTurn() == true)
            throw new DieAlreadyPlacedException("");
        if (wp.isGridEmpty()) {
            try {
                wp.placeDie(d, Constants.WINDOW_PATTERN_COLUMN_NUMBER * y + x, PlacementConstraint.initialConstraint());
            } catch (InvalidPlacementException e) {
                throw e;
            }
        }
        else{
            try {
                wp.placeDie(d,Constants.WINDOW_PATTERN_COLUMN_NUMBER*y+x);
            } catch (InvalidPlacementException e) {
                throw e;
            }
        }
        getPlayer(playerID).setDiePlacedInThisTurn(true);
        this.game.getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
    }

    @Override
    public void useToolCard(int toolCardsIndex, JsonObject data) throws RemoteException, InvalidEffectResultException, InvalidEffectArgumentException {
        this.game.getToolCards().get(toolCardsIndex).effect(data);
    }
}
