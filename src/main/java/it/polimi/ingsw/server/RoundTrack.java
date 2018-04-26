package it.polimi.ingsw.server;

import java.util.List;
import java.util.Vector;


public class RoundTrack {

    private List<Die> dice;
    private int currentRound;

    public RoundTrack() {
        this.currentRound = 1;
    }

    public List<Die> getDice() {
        if (this.dice == null)
            this.dice = new Vector<>();
        return this.dice;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void incrementRound() throws GameOverException {
        if (this.currentRound >= 10)
            throw new GameOverException();
        this.currentRound++;
    }

    public void putDie(List<Die> fromDraftPool) {
        this.getDice().addAll(fromDraftPool);
    }
}


class Die {     // TODO Remove mock up

}