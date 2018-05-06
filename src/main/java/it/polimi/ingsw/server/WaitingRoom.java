package it.polimi.ingsw.server;

import java.util.*;


// This class represent the waiting room of the server
public class WaitingRoom extends Observable {

    private static WaitingRoom instance;
    private List<String> nicknames;
    private long timeout;
    private Timer timer;

    private WaitingRoom() {
        this.timeout = 2;  // TODO configuration file
    }

    public static synchronized WaitingRoom getInstance() {
        if (instance == null)
            instance = new WaitingRoom();
        return instance;
    }

    public synchronized List<String> getNicknames() {
        if (this.nicknames == null)
            this.nicknames = new Vector<>();
        return this.nicknames;
    }

    private synchronized Timer getTimer() {
        if (this.timer == null)
            this.timer = new Timer(true);
        return this.timer;
    }

    private synchronized void cancelTimer() {
        getTimer().cancel();
        getTimer().purge();
        this.timer = null;
    }

    // Add player to this waiting room.
    // Game creation occurs when timer expires or when 4 players are reached.
    public synchronized boolean addPlayer(String nickname) {
        if (this.getNicknames().contains(nickname))
            return false;
        this.getNicknames().add(nickname);
        if (this.getNicknames().size() == 2) {
            this.cancelTimer();
            this.getTimer().schedule(new TimerTask() {
                    public void run() {
                        createGame();
                    }
                }, this.timeout * 1000);        // delay is in milliseconds
        } else if (this.getNicknames().size() == 4) {
            this.createGame();
        }
        return true;
    }

    // Create a new game with the first N players of the list.
    // The timer is canceled and SagradaServer is notified.
    private synchronized void createGame() {
        List<String> currentNicknames = new ArrayList<>(this.getNicknames().subList(0, this.getNicknames().size()));
        this.getNicknames().removeAll(currentNicknames);
        this.cancelTimer();
        this.setChanged();      // Needed to make notifyObservers work
        this.notifyObservers(new Game(currentNicknames));
    }

}
