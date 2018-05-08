package it.polimi.ingsw.server;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.objectivecards.ObjectiveCard;
import it.polimi.ingsw.patterncards.WindowPattern;
import it.polimi.ingsw.rmi.ServerAPI;
import it.polimi.ingsw.toolcards.ToolCard;
import java.util.*;
import java.util.stream.Collectors;


public class ServerEndPoint implements ServerAPI {

    private Game game;
    private boolean gameSet = false;

    ServerEndPoint() {}

    void setGame(Game game) {
        if (!this.gameSet) {
            this.game = game;
            this.gameSet = true;
        } else {
            throw new IllegalStateException("Game already set");
        }
    }

    private Game getGame() {
        if (this.game == null) throw new IllegalStateException("Game not set yet");
        return this.game;
    }

    public List<String> getCurrentPlayers() {
        return this.getGame().getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    @Override
    public void nextTurn() {
        this.getGame().getTurnManager().nextTurn();
    }

    @Override
    public void registerTimerForWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().getTimerReference().addObserver(observer);
    }

    @Override
    public void registerTimerForTurnManager(Observer observer) {
        this.getGame().getTurnManager().getTimerReference().addObserver(observer);
    }

    @Override
    public Map<String, Integer> getFinalScores() {
        return this.getGame().getFinalScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getNickname(),
                        Map.Entry::getValue
                ));
    }

    @Override
    public List<ObjectiveCard> getPublicObjectiveCards() {
        return this.getGame().getPublicObjectiveCards();
    }

    @Override
    public List<ToolCard> getToolCards() {
        return this.getGame().getToolCards();
    }

    @Override
    public Player getYourOwnPlayerObject(String nickname) {
        Optional<Player> result = this.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object with specified nickname found");
    }

    @Override
    public int getFavorTokensOf(String nickname) {
        Optional<Integer> result = this.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getFavorTokens)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve favor tokens because no Player object with specified nickname has been found");
    }

    @Override
    public WindowPattern getWindowPatternOf(String nickname) {
        Optional<WindowPattern> result = this.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getWindowPattern)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve WindowPattern because no Player object with specified nickname has been found");
    }

    @Override
    public int getCurrentRound() {
        return this.getGame().getRoundTrack().getCurrentRound();
    }

    @Override
    public List<Die> getRoundTrackDice() {
        return new Vector<>(this.getGame().getRoundTrack().getDice());
    }

    @Override
    public boolean addPlayer(String nickname) {
        return WaitingRoom.getInstance().addPlayer(nickname);
    }

    @Override
    public List<String> getWaitingPlayers() {
        return new Vector<>(WaitingRoom.getInstance().getNicknames());
    }

    @Override
    public List<Die> getDraftPool() {
        return this.getGame().getDiceGenerator().getDraftPool();
    }
}
