package it.polimi.ingsw.server;


import java.util.*;
import java.util.stream.*;


public class TurnManager extends Observable {

    private List<Player> players;
    private List<Integer> playersOrder;
    private int index;

    public TurnManager(List<Player> players) {
        this.players = players;
        Stream<Integer> forwardRange = IntStream.range(0, this.getNumberOfPlayers()).boxed();
        Stream<Integer> backRange = IntStream.range(0, this.getNumberOfPlayers()).boxed().sorted(Collections.reverseOrder());
        this.playersOrder = Stream.concat(forwardRange, backRange).collect(Collectors.toList());
        this.index = 0;
        this.setActivePlayer(this.getCurrentPlayer());
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

    private void setActivePlayer(Player player) {
        for (Player p : this.players) {
            p.setActive(p.equals(player));
        }
    }

    public void nextTurn() {
        this.index++;
        if (this.index == this.playersOrder.size()) {
            this.index = 0;
            Collections.rotate(this.players, -1);   // shift starting player
            this.setChanged();
            this.notifyObservers();
        }
        this.setActivePlayer(this.getCurrentPlayer());
    }

}
