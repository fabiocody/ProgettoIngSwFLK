package it.polimi.ingsw.server;

import java.util.List;
import java.util.Vector;


// This class represent the Round Track
public class RoundTrack {

    private List<Die> dice;     // Dice placed on the round track. The order doesn't matter.
    private int currentRound;
    private boolean gameOver;

    public RoundTrack() {
        this.currentRound = 1;
        this.gameOver = false;
    }

    public List<Die> getDice() {
        if (this.dice == null)
            this.dice = new Vector<>();
        return this.dice;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    // Increment round. Throw GameOverException when the 10th round is over.
    public void incrementRound() {
        if (this.currentRound == 10 && !gameOver) {
            this.gameOver = true;
        } else if (!gameOver) {
            this.currentRound++;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Place what remains of the Draft Pool into the dice list
    public void putDie(List<Die> fromDraftPool) {
        this.getDice().addAll(fromDraftPool);
    }
}


class Die {     // TODO Remove mock up

}