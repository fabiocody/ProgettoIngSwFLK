package it.polimi.ingsw;

import it.polimi.ingsw.server.RoundTrack;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class RoundTrackTest {

    @Test
    void roundTrackTest() {
        RoundTrack roundTrack = new RoundTrack();
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 0; i < 10; i++) {
            roundTrack.incrementRound();
            assertTrue(roundTrack.getCurrentRound() <= 10);
        }
        assertEquals(10, roundTrack.getCurrentRound());
    }

}
