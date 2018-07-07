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
    private List<List<Die>> dice;
    private int currentRound;
    private boolean gameOver;

    /**
     * @author Fabio Codiglioni
     */
    RoundTrack() {
        this.currentRound = 1;
        this.gameOver = false;
        if (this.dice == null){
            this.dice = new Vector<>(NUMBER_OF_ROUNDS);
            for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
                dice.add(new Vector<>(MAX_NUMBER_OF_PLAYERS + 1));
            }
        }
    }

    public List<List<Die>> getDice(){
        return dice;
    }


    /**
     * @return true if the RoundTrack has no dices, false otherwise
     * @author Luca dell'Oglio
     */
    public boolean isRoundTrackEmpty(){
        return getFlattenedDice().isEmpty() || getDice() == null;
    }

    /**
     * This method returns the list of dice placed on the Round Track, and also instantiate the underlying data structure.
     *
     * @author Fabio Codiglioni
     * @return the list of dice placed on the Round Track.
     */
    public List<Die> getFlattenedDice() {
        return dice.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * @author Fabio Codiglioni
     * @return the number of the current round.
     */
    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentRoundDiceIndex() {
        return currentRound - 2;
    }

    /**
     * This method increments the round number, and notify the Game about it.
     *
     * @author Fabio Codiglioni
     */
    public void incrementRound() {
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

    /**
     * @author Fabio Codiglioni
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * This method transfers all the dice from the Draft Pool to the Round Track. The Draft Pool is left empty when this method returns.
     *
     * @author Fabio Codiglioni
     * @param draftPool the Draft Pool from which transferring the dice.
     */
    public void putDice(List<Die> draftPool) {
        this.dice.get(this.getCurrentRoundDiceIndex()).addAll(draftPool);
        draftPool.clear();
    }

    /**
     * This method swaps the provided die with the die at <code>roundTrackIndex</code>.
     *
     * @param d the die to be swapped (should come from the Draft Pool)
     * @param roundTrackIndex the flattened index of the die to be swapped
     * @return the die previously at <code>roundTrackIndex</code>
     */
    public Die swapDice(Die d, int roundTrackIndex) {
        int index = 0;
        int round = 0;
        int i = 0;
        for (; round < NUMBER_OF_ROUNDS && index < roundTrackIndex; round++) {
            List<Die> roundDice = dice.get(round);
            for (i = 0; i < roundDice.size() && index < roundTrackIndex; i++, index++);
        }
        round -= 1;
        if (index == roundTrackIndex) {
            Die roundTrackDie = dice.get(round).remove(i);
            dice.get(round).add(i, d);
            return roundTrackDie;
        } else {
            throw new NoSuchElementException("No dice on RoundTrack at roundTrackIndex " + roundTrackIndex);
        }
    }

    public String toString() {
        StringBuilder roundTrackCli = new StringBuilder();
        Optional<Integer> maxDiceInRound = getDice().stream()
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
                    if (i < this.dice.get(j).size() && this.dice.get(j).get(i) != null)
                        roundTrackCli.append(this.dice.get(j).get(i)).append(" ");
                    else
                        roundTrackCli.append("    ");
                }
                roundTrackCli.append("\n");
            }
        });
        return roundTrackCli.toString();
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
            } else if (arg.equals(NotificationMessages.GAME_INTERRUPTED)) {
                this.gameOver = true;
                this.setChanged();
                this.notifyObservers(arg);
            }
        }
    }

}
