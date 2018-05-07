package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class RoundTrackTest {

    @Test
    void roundTrackTest() {
        RoundTrack roundTrack = new RoundTrack();
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 1; i < 10; i++) {
            int previousRound = roundTrack.getCurrentRound();
            roundTrack.incrementRound();
            assertEquals(previousRound + 1, roundTrack.getCurrentRound());
        }
        roundTrack.incrementRound();
        assertEquals(10, roundTrack.getCurrentRound());
    }

}
