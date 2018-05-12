package it.polimi.ingsw.server;

import it.polimi.ingsw.util.CountdownTimer;
import java.util.*;
import java.util.stream.*;


public class TurnManager extends Observable {
    // Is observed by RoundTrack

    private List<Player> players;
    private List<Integer> playersOrder;
    private int index;
    private CountdownTimer timer;
    private int timeout = 30;     // TODO Load from file

    public TurnManager(List<Player> players) {
        this.players = players;
        Stream<Integer> forwardRange = IntStream.range(0, this.getNumberOfPlayers()).boxed();
        Stream<Integer> backRange = IntStream.range(0, this.getNumberOfPlayers()).boxed().sorted(Collections.reverseOrder());
        this.playersOrder = Stream.concat(forwardRange, backRange).collect(Collectors.toList());
        this.index = 0;
        this.timer = new CountdownTimer("TurnManager", this.timeout);
        this.setActivePlayer(this.getCurrentPlayer());
        // TODO Set timer for first turn of first round
    }

    private int getNumberOfPlayers() {
        return this.players.size();
    }

    private int getCurrentPlayerIndex() {
        return this.playersOrder.get(index);
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.getCurrentPlayerIndex());
    }

    public CountdownTimer getTimer() {
        return this.timer;
    }

    private void setActivePlayer(Player player) {
        for (Player p : this.players) {
            p.setActive(p.equals(player));
        }
    }

    public boolean isFirstHalfOfRound() {
        return this.index < this.playersOrder.size() / 2;
    }

    public boolean isSecondHalfOfRound() {
        return this.index >= this.playersOrder.size() / 2;
    }

    public void nextTurn() {
        this.timer.cancel();
        this.index++;
        if (this.index == this.playersOrder.size()) {
            this.index = 0;
            Collections.rotate(this.players, -1);   // shift starting player
            this.setChanged();
            this.notifyObservers();
        }
        this.setActivePlayer(this.getCurrentPlayer());
        this.timer.schedule(this::nextTurn, this.timeout);
    }


}
