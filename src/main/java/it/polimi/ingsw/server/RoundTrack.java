package it.polimi.ingsw.server;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.util.Constants;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


/**
 * This class represent the Round Track.
 *
 * @author Fabio Codiglioni
 */
public class RoundTrack extends Observable implements Observer {
    // Observes TurnManager
    // Is observed by Game

    // Attributes
    //private List<Die> roundTrack;     // Dice placed on the round track. The order doesn't matter.
    private Vector<Die>[] roundTrack;
    private int currentRound;
    private boolean gameOver;

    // Locks
    private final Object diceLock = new Object();
    private final Object currentRoundLock = new Object();
    private final Object gameOverLock = new Object();

    /**
     * @author Fabio Codiglioni
     */
    public RoundTrack() {
        this.currentRound = 1;
        this.gameOver = false;
    }

    public Vector<Die>[] getVectorRoundTrack(){
        return roundTrack;
    }

    /**
     * This method returns the list of roundTrack placed on the Round Track, and also instantiate the underlying data structure.
     *
     * @author Fabio Codiglioni
     * @return the list of roundTrack placed on the Round Track.
     */
    public List<Die> getAllDice() {
        synchronized (diceLock) {
            if (this.roundTrack == null){
                this.roundTrack = new Vector[Constants.NUMBER_OF_TURNS];
                for (int i = 0; i < Constants.NUMBER_OF_TURNS; i++){
                    roundTrack[i] = new Vector<>();
                }
            }
            List<Die> allDice = new Vector<>();
            for (int i = 0; i < Constants.NUMBER_OF_TURNS; i++){
                allDice.addAll(roundTrack[i]);
            }
            return allDice;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the number of the current round.
     */
    public synchronized int getCurrentRound() {
        synchronized (currentRoundLock) {
            return currentRound;
        }
    }

    /**
     * This method increments the round number, and notify the Game about it.
     *
     * @author Fabio Codiglioni
     */
    public void incrementRound() {
        synchronized (currentRoundLock) {
            synchronized (gameOverLock) {
                if (this.currentRound == 10 && !gameOver) {
                    this.gameOver = true;
                    this.setChanged();
                    this.notifyObservers("Game over");
                }
                if (!gameOver) {
                    this.currentRound++;
                    this.setChanged();
                    this.notifyObservers("Round incremented");
                }
            }
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the game is over.
     */
    public synchronized boolean isGameOver() {
        synchronized (gameOverLock) {
            return gameOver;
        }
    }

    /**
     * This method transfers all the roundTrack from the Draft Pool to the Round Track. The Draft Pool is left empty when this method returns.
     *
     * @author Fabio Codiglioni
     * @param fromDraftPool the Draft Pool from which transferring the roundTrack.
     */
    public void putDice(List<Die> fromDraftPool) {
        synchronized (diceLock) {
            this.roundTrack[this.currentRound - 1].addAll(fromDraftPool);
            fromDraftPool.clear();
        }
    }

    /**
     * This method receives the updates from <code>TurnManager</code>.
     *
     * @param o the observable which sent the notification.
     * @param arg the argument of the notification.
     */
    public void update(Observable o, Object arg) {
        if (o instanceof TurnManager) this.incrementRound();
    }

}
