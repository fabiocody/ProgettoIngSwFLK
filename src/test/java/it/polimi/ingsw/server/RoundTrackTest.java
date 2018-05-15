package it.polimi.ingsw.server;

import it.polimi.ingsw.dice.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class RoundTrackTest {

    private static RoundTrack roundTrack;

    @BeforeAll
    static void setup() {
        roundTrack = new RoundTrack();
    }

    @Test
    void roundTrackTest() {
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 1; i < 10; i++) {
            int previousRound = roundTrack.getCurrentRound();
            roundTrack.incrementRound();
            assertEquals(previousRound + 1, roundTrack.getCurrentRound());
        }
        roundTrack.incrementRound();
        assertEquals(10, roundTrack.getCurrentRound());
    }

    @Test
    void putDiceTest() {
        DiceGenerator diceGenerator = new DiceGenerator(4);
        diceGenerator.generateDraftPool();
        List<Die> draftPool = new ArrayList<>(diceGenerator.getDraftPool());
        roundTrack.putDice(diceGenerator.getDraftPool());
        for (Die die : draftPool) {
            assertTrue(roundTrack.getDice().contains(die));
        }
    }

}
