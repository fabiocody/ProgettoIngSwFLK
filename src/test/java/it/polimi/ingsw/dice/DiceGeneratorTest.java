package it.polimi.ingsw.dice;

import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.util.Colors;
import it.polimi.ingsw.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.dice.*;

import java.util.*;


class DiceGeneratorTest {

    private static DiceGenerator generator;

    @BeforeAll
    static void setup() {
        generator = new DiceGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
    }

    @Test
    void valueTest() {
        Die d = generator.draw();
        assertTrue(d.getValue() >= 1 && d.getValue() <= 6);
    }

    @Test
    void colorTest() {
        Die d = generator.draw();
        Colors color = d.getColor();
        assertTrue(color.equals(Colors.RED) || color.equals(Colors.GREEN) || color.equals(Colors.YELLOW) || color.equals(Colors.BLUE) || color.equals(Colors.PURPLE));
    }

    @Test
    void drawnTest() {
        Die d = generator.draw();
        assertNotNull(d);
    }

    @Test
    void maxDiceGenerated() {
        DiceGenerator generator = new DiceGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        for (int i = 0; i<5*Constants.MAX_NUMBER_OF_SAME_COLOR_DICE; i++) {
            assertNotNull(generator.draw());
        }
        assertThrows(NoMoreDiceException.class, generator::draw);
    }

    @Test
    void maxDiceColorGenerator() {
        DiceGenerator generator = new DiceGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        Map<Colors, Integer> generatedDice = new HashMap<>();
        generatedDice.put(Colors.RED, 0);
        generatedDice.put(Colors.GREEN, 0);
        generatedDice.put(Colors.YELLOW, 0);
        generatedDice.put(Colors.BLUE, 0);
        generatedDice.put(Colors.PURPLE, 0);

        for (int i = 0; i<5*Constants.MAX_NUMBER_OF_SAME_COLOR_DICE; i++) {
            Die d = generator.draw();
            generatedDice.replace(d.getColor(), generatedDice.get(d.getColor())+1);
        }

        for (Integer v : generatedDice.values())
            assertEquals(Constants.MAX_NUMBER_OF_SAME_COLOR_DICE, v.intValue());
    }

    @Test
    void putAwayTest() {
        DiceGenerator generator = new DiceGenerator(Constants.MAX_NUMBER_OF_PLAYERS);

        generator.generateDraftPool();
        Die d = generator.getDraftPool().get(1);
        assertNotNull(d);
        generator.putAway(d);       // There's an assertion in the method
    }


}
