package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.dice.DiceGenerator;

public class DieTest {

    private int value;
    private Colors color;


    @Test
    public void valueTest() {
        Die testDie = new Die();
        value = testDie.getValue();
        assertTrue(value>0 && value <=6);
    }

    @Test
    public void colorTest() {
        Die testDie = new Die();
        color = testDie.getColor();
        assertTrue(color.equals(Colors.RED) ^ color.equals(Colors.GREEN) ^ color.equals(Colors.YELLOW) ^
                color.equals(Colors.BLUE) ^ color.equals(Colors.PURPLE));
    }

    @Test
    public void generateTest() {
        DiceGenerator diceGenerator = new DiceGenerator(4);
        Die testDie = diceGenerator.generate();
        assertNotNull(testDie);
    }
}
