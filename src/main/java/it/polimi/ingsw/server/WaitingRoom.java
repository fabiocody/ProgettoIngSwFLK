package it.polimi.ingsw.server;

import it.polimi.ingsw.util.CountdownTimer;

import java.util.*;


// This class represent the waiting room of the server
public class WaitingRoom extends Observable {
    // Is observed by SagradaServer

    private static WaitingRoom instance;
    private List<Player> waitingPlayers;
    private int timeout = 60;
    private CountdownTimer timer;
    private boolean playerAdded = false;

    private WaitingRoom() {
        this.timer = new CountdownTimer("WaitingRoom", this.timeout);
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

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public CountdownTimer getTimer() {
        return this.timer;
    }

    // Add player to this waiting room.
    // Game creation occurs when timer expires or when 4 players are reached.
    public synchronized UUID addPlayer(String nickname) throws LoginFailedException {
        if (SagradaServer.getInstance().isNicknameUsed(nickname))
            throw new LoginFailedException(nickname);
        Player player = new Player(nickname);
        this.getWaitingPlayers().add(player);
        this.setChanged();
        this.notifyObservers(this.getWaitingPlayers());
        if (this.getWaitingPlayers().size() == 2) {
            this.timer.cancel();
            this.timer.schedule(this::createGame, this.timeout);
        } else if (this.getWaitingPlayers().size() == 4) {
            new Thread(this::createGame).start();
        }
        playerAdded = true;
        notifyAll();
        return player.getId();
    }

    synchronized void removePlayer(String nickname) {
        Optional<Player> player = this.getWaitingPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst();
        if (player.isPresent()) {
            this.getWaitingPlayers().remove(player.get());
            if (this.getWaitingPlayers().size() < 2)
                this.timer.cancel();
            this.setChanged();
            this.notifyObservers(this.getWaitingPlayers());
        }
    }

    // Create a new game with the first N players of the list.
    // The timer is canceled and SagradaServer is notified.
    private synchronized void createGame() {
        while (!playerAdded) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        playerAdded = false;
        this.timer.cancel();
        this.setChanged();      // Needed to make notifyObservers work
        this.notifyObservers(new Game(this.getWaitingPlayers()));
        this.getWaitingPlayers().clear();
    }

}
