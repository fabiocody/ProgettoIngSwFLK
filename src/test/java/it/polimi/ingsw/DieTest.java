package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.dice.*;


public class DieTest {

    private static DiceGenerator generator;

    @BeforeAll
    static void setup() {
        generator = new DiceGenerator(4);
    }


    @Test
    public void valueTest() {
        Die d = generator.generate();
        assertTrue(d.getValue() >= 1 && d.getValue() <= 6);
    }

    @Test
    public void colorTest() {
        Die d = generator.generate();
        Colors color = d.getColor();
        assertTrue(color.equals(Colors.RED) || color.equals(Colors.GREEN) || color.equals(Colors.YELLOW) || color.equals(Colors.BLUE) || color.equals(Colors.PURPLE));
    }

    @Test
    public void generateTest() {
        Die d = generator.generate();
        assertNotNull(d);
    }
}
