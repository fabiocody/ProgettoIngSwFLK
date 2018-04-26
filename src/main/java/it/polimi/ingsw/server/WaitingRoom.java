package it.polimi.ingsw.server;

import java.util.*;


public class WaitingRoom extends Observable {

    private List<String> nicknames;
    private int timeout;
    private Timer timer;

    public WaitingRoom() {
        this.timeout = 2;  // TODO configuration file
    }

    public List<String> getNicknames() {
        if (this.nicknames == null)
            this.nicknames = new Vector<>();
        return this.nicknames;
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
                }, this.timeout * 1000);
        } else if (this.getNicknames().size() == 4) {
            this.createGame();
        }
        return true;
    }

    private synchronized void createGame() {
        List<String> currentNicknames = new ArrayList<>(this.getNicknames().subList(0, this.getNicknames().size()));
        this.getNicknames().removeAll(currentNicknames);
        this.cancelTimer();
        this.setChanged();
        this.notifyObservers(new Game(currentNicknames));
    }

}
