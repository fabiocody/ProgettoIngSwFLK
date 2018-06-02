package it.polimi.ingsw.server;

import it.polimi.ingsw.model.dice.*;
import it.polimi.ingsw.util.Constants;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class RoundTrackTest {

    private static RoundTrack roundTrack;

    @BeforeAll
    static void setup() {
        roundTrack = new RoundTrack();
        roundTrack.getAllDice(); //initialization
    }

    @Test
    void roundTrackTest() {
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 1; i < Constants.NUMBER_OF_TURNS; i++) {
            int previousRound = roundTrack.getCurrentRound();
            roundTrack.incrementRound();
            assertEquals(previousRound + 1, roundTrack.getCurrentRound());
        }
        roundTrack.incrementRound();
        assertEquals(Constants.NUMBER_OF_TURNS, roundTrack.getCurrentRound());
    }

    @Test
    void putDiceTest() {
        DiceGenerator diceGenerator = new DiceGenerator(Constants.MAX_PLAYER_NUMBER);
        diceGenerator.generateDraftPool();
        List<Die> draftPool = new ArrayList<>(diceGenerator.getDraftPool());
        roundTrack.putDice(diceGenerator.getDraftPool());
        for (Die die : draftPool) {
            assertTrue(roundTrack.getAllDice().contains(die));
        }
    }

}
