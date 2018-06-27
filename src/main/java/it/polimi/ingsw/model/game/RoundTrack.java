package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.NotificationMessages;

import java.util.*;
import java.util.stream.Collectors;
import static it.polimi.ingsw.shared.util.Constants.*;
import static org.fusesource.jansi.Ansi.ansi;


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

    public synchronized int getCurrentRoundDiceIndex() {
        synchronized (currentRoundLock) {
            return currentRound - 2;
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
                this.currentRound++;
                if (this.currentRound > Constants.NUMBER_OF_ROUNDS && !gameOver) {
                    this.gameOver = true;
                    this.setChanged();
                    this.notifyObservers(NotificationMessages.GAME_OVER);
                }
                if (!gameOver) {
                    this.setChanged();
                    this.notifyObservers(NotificationMessages.ROUND_INCREMENTED);
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
            this.roundTrackDice[this.getCurrentRoundDiceIndex()].addAll(fromDraftPool);
            fromDraftPool.clear();
        }
    }

    public String toString() {
        synchronized (diceLock) {
            StringBuilder roundTrackCli = new StringBuilder();
            Optional<Integer> maxDiceInRound = Arrays.stream(getRoundTrackDice())
                    .map(List::size)
                    .max(Comparator.naturalOrder());
            maxDiceInRound.ifPresent(max -> {
                for (int i = 1; i <= NUMBER_OF_ROUNDS; i++)
                    roundTrackCli.append(' ')
                            .append(i == currentRound ? ansi().fgBrightGreen().a(i).reset() : i)
                            .append("  ");
                roundTrackCli.append('\n');
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
        if (o instanceof TurnManager) {
            if (arg.equals(NotificationMessages.ROUND_INCREMENTED)) {
                this.incrementRound();
            } /*else if (arg.equals(NotificationMessages.GAME_OVER)) {
                currentRound++;
                this.gameOver = true;
                this.setChanged();
                this.notifyObservers(arg);
            }*/ else if (arg.equals(NotificationMessages.GAME_INTERRUPTED)) {
                this.gameOver = true;
                this.setChanged();
                this.notifyObservers(arg);
            }
        }
    }

}
