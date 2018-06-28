package it.polimi.ingsw.model.dice;

import it.polimi.ingsw.model.Colors;
import java.util.concurrent.ThreadLocalRandom;
import static org.fusesource.jansi.Ansi.ansi;


public class Die {

    private int value;
    private final Colors color;

    public Die(Colors color, int value) {
        this.color = color;
        this.value = value;
    }

    public Die(Colors color) {
        this.color = color;
        this.roll();
    }

    /**
     * @author Kai de Gast
     * @return the value of the die
     */
    public int getValue() {
            return this.value;
    }

    /**
     * @author Kai de Gast
     * @return the color of the die
     */
    public Colors getColor() {
            return this.color;
    }

    /**
     * This method sets a specified value as the value of the die
     *
     * @author Kai de Gast
     * @param value the new value of the die
     */
    public void setValue(int value) {
            this.value = value;
    }

    /**
     * This method generates a random number between 1 and 6 (inclusive) and sets it as the value of the die
     *
     * @author Kai de Gast
     */
    public void roll() {
            this.setValue(ThreadLocalRandom.current().nextInt(1, 7));
    }

    /**
     * This method returns a String representing the die
     *
     * @author Kai de Gast
     * @return "[x]" colored as the die, where x is the value
     */
    public String toString() {
        return ansi().fg(this.color.getJAnsiColor()).a("[" + this.value + "]").reset().toString();
    }
}
