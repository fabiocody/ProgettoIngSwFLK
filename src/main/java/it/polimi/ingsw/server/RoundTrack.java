package it.polimi.ingsw.server;

import it.polimi.ingsw.model.dice.DiceGenerator;
import it.polimi.ingsw.model.dice.Die;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import static it.polimi.ingsw.util.Constants.*;


/**
 * This class represent the Round Track.
 *
 * @author Fabio Codiglioni
 */
public class RoundTrack extends Observable implements Observer {
    // Observes TurnManager
    // Is observed by Game

    // Attributes
    private List<Die>[] roundTrackDice;
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
        if (this.roundTrackDice == null){
            this.roundTrackDice = new Vector[NUMBER_OF_ROUNDS];
            for (int i = 0; i < NUMBER_OF_ROUNDS; i++){
                roundTrackDice[i] = new Vector<>();
            }
        }
    }

    public List<Die>[] getRoundTrackDice(){
        synchronized (diceLock) {
            return roundTrackDice;
        }
    }

    /**
     * This method returns the list of roundTrackDice placed on the Round Track, and also instantiate the underlying data structure.
     *
     * @author Fabio Codiglioni
     * @return the list of roundTrackDice placed on the Round Track.
     */
    public List<Die> getAllDice() {
        synchronized (diceLock) {
            return Arrays.stream(this.roundTrackDice).flatMap(Collection::stream).collect(Collectors.toList());
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
     * This method transfers all the roundTrackDice from the Draft Pool to the Round Track. The Draft Pool is left empty when this method returns.
     *
     * @author Fabio Codiglioni
     * @param fromDraftPool the Draft Pool from which transferring the roundTrackDice.
     */
    public void putDice(List<Die> fromDraftPool) {
        synchronized (diceLock) {
            this.roundTrackDice[this.currentRound - 1].addAll(fromDraftPool);
            fromDraftPool.clear();
        }
    }

    public String toString() {
        synchronized (diceLock) {
            StringBuilder roundTrackCli = new StringBuilder("$roundTrack$");
            Optional<Integer> maxDiceInRound = Arrays.stream(getRoundTrackDice())
                    .map(List::size)
                    .max(Comparator.naturalOrder());
            maxDiceInRound.ifPresent(max -> {
                for (int i = 0; i < max; i++) {
                    for (int j = 0; j < NUMBER_OF_ROUNDS; j++) {
                        if (i < this.roundTrackDice[j].size() && this.roundTrackDice[j].get(i) != null)
                            roundTrackCli.append(this.roundTrackDice[j].get(i)).append(" ");
                        else
                            roundTrackCli.append("    ");
                    }
                    roundTrackCli.append("\n");
                }
            });
            return roundTrackCli.toString();
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
