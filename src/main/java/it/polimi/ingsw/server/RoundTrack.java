package it.polimi.ingsw.server;

import it.polimi.ingsw.dice.Die;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


// This class represent the Round Track
public class RoundTrack extends Observable implements Observer {

    // Attributes
    private List<Die> dice;     // Dice placed on the round track. The order doesn't matter.
    private int currentRound;
    private boolean gameOver;

    // Locks
    private final Object diceLock = new Object();
    private final Object currentRoundLock = new Object();
    private final Object gameOverLock = new Object();

    public RoundTrack() {
        this.currentRound = 1;
        this.gameOver = false;
    }

    public List<Die> getDice() {
        synchronized (diceLock) {
            if (this.dice == null)
                this.dice = new Vector<>();
            return this.dice;
        }
    }

    public synchronized int getCurrentRound() {
        synchronized (currentRoundLock) {
            return currentRound;
        }
    }

    // Increment round. Throw GameOverException when the 10th round is over.
    public void incrementRound() {
        synchronized (currentRoundLock) {
            synchronized (gameOverLock) {
                if (this.currentRound == 10 && !gameOver) {
                    this.gameOver = true;
                    this.setChanged();
                    this.notifyObservers("Game over");
                } else if (!gameOver) {
                    this.currentRound++;
                    this.setChanged();
                    this.notifyObservers("Round incremented");
                }
            }
        }
    }

    public synchronized boolean isGameOver() {
        synchronized (gameOverLock) {
            return gameOver;
        }
    }

    // Place what remains of the Draft Pool into the dice list
    public void putDie(List<Die> fromDraftPool) {
        synchronized (diceLock) {
            this.getDice().addAll(fromDraftPool);
            fromDraftPool.clear();
        }
    }

    public void update(Observable o, Object arg) {
        if (o instanceof TurnManager) this.incrementRound();
    }

}
