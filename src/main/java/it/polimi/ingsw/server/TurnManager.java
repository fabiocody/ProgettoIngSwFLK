package it.polimi.ingsw.server;


import java.util.*;
import java.util.stream.*;


public class TurnManager extends Observable {

    private List<Player> players;
    private List<Integer> playersOrder;
    private int index;
    private Timer timer;
    private long timeout = 30;     // TODO Load from file

    public TurnManager(List<Player> players) {
        this.players = players;
        Stream<Integer> forwardRange = IntStream.range(0, this.getNumberOfPlayers()).boxed();
        Stream<Integer> backRange = IntStream.range(0, this.getNumberOfPlayers()).boxed().sorted(Collections.reverseOrder());
        this.playersOrder = Stream.concat(forwardRange, backRange).collect(Collectors.toList());
        this.index = 0;
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

    private void setActivePlayer(Player player) {
        for (Player p : this.players) {
            p.setActive(p.equals(player));
        }
    }

    public void nextTurn() {
        this.cancelTimer();
        this.index++;
        if (this.index == this.playersOrder.size()) {
            this.index = 0;
            Collections.rotate(this.players, -1);   // shift starting player
            this.setChanged();
            this.notifyObservers();
        }
        this.setActivePlayer(this.getCurrentPlayer());
        this.getTimer().schedule(new TimerTask() {
            public void run() {
                nextTurn();
            }
        }, this.timeout * 1000);        // delay is in milliseconds
    }

    private Timer getTimer() {
        if (this.timer == null)
            this.timer = new Timer(true);
        return this.timer;
    }

    private void cancelTimer() {
        getTimer().cancel();
        getTimer().purge();
        this.timer = null;
    }

}
