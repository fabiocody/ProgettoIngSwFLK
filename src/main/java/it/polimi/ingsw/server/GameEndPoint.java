package it.polimi.ingsw.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.objectivecards.ObjectiveCard;
import it.polimi.ingsw.patterncards.WindowPattern;
import it.polimi.ingsw.rmi.GameAPI;
import it.polimi.ingsw.toolcards.InvalidEffectArgumentException;
import it.polimi.ingsw.toolcards.InvalidEffectResultException;
import it.polimi.ingsw.toolcards.ToolCard;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;


public class GameEndPoint implements GameAPI {

    private Game game;
    private boolean gameSet = false;

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
    public void registerTimerForTurnManager(Observer observer) {
        this.game.getTurnManager().getTimerReference().addObserver(observer);
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
    public Player getYourOwnPlayerObject(UUID id) {
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object with specified nickname found");
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
    public void placeDie(int draftPoolIndex, int x, int y) throws RemoteException {
        // TODO
    }

    @Override
    public void useToolCard(int toolCardsIndex, JsonObject data) throws RemoteException, InvalidEffectResultException, InvalidEffectArgumentException {
        // TODO
    }
}
