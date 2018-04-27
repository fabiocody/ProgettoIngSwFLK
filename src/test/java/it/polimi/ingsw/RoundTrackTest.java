package it.polimi.ingsw;


import it.polimi.ingsw.server.GameOverException;
import it.polimi.ingsw.server.RoundTrack;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RoundTrackTest {

    @Test
    void roundTrackTest() {
        RoundTrack roundTrack = new RoundTrack();
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 0; i < 10; i++) {
            try {
                roundTrack.incrementRound();
                assertTrue(roundTrack.getCurrentRound() <= 10);
            } catch (GameOverException e) {
                assertEquals(10, roundTrack.getCurrentRound());
            }
        }
    }

}
