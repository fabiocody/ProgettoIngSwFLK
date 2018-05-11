package it.polimi.ingsw.server;

import it.polimi.ingsw.util.CountdownTimer;

import java.util.*;


// This class represent the waiting room of the server
public class WaitingRoom extends Observable {
    // Is observed by SagradaServer

    private static WaitingRoom instance;
    private List<Player> waitingPlayers;
    private int timeout;
    private CountdownTimer timer;

    private WaitingRoom() {
        this.timeout = 2;  // TODO configuration file
    }

    public static synchronized WaitingRoom getInstance() {
        if (instance == null)
            instance = new WaitingRoom();
        return instance;
    }

    public synchronized List<Player> getWaitingPlayers() {
        if (this.waitingPlayers == null)
            this.waitingPlayers = new Vector<>();
        return this.waitingPlayers;
    }

    CountdownTimer getTimerReference() {
        return this.timer;
    }

    private CountdownTimer getTimer() {
        if (this.timer == null)
            this.timer = new CountdownTimer(this.timeout);
        return this.timer;
    }

    private void cancelTimer() {
        getTimer().cancel();
        this.timer = null;
    }

    // Add player to this waiting room.
    // Game creation occurs when timer expires or when 4 players are reached.
    public synchronized UUID addPlayer(String nickname) {
        for (Player p : this.getWaitingPlayers()) {     // TODO Make functional (great again)
            if (p.getNickname().equals(nickname))
                return null;
        }
        Player player = new Player(nickname);
        this.getWaitingPlayers().add(player);
        if (this.getWaitingPlayers().size() == 2) {
            this.cancelTimer();
            this.getTimer().schedule(this::createGame);
        } else if (this.getWaitingPlayers().size() == 4) {
            this.createGame();
        }
        return player.getId();
    }

    // Create a new game with the first N players of the list.
    // The timer is canceled and SagradaServer is notified.
    private synchronized void createGame() {
        this.cancelTimer();
        this.setChanged();      // Needed to make notifyObservers work
        this.notifyObservers(new Game(this.getWaitingPlayers()));
        this.getWaitingPlayers().clear();
    }

}
