package it.polimi.ingsw.model.dice;

import it.polimi.ingsw.util.Colors;
import java.util.concurrent.ThreadLocalRandom;
import static it.polimi.ingsw.util.Ansi.*;


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
    public synchronized int getValue() {
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
     * @param newVal the new value of the die
     */
    public synchronized void setValue(int newVal) {
            this.value = newVal;
    }

    /**
     * This method generates a random number between 1 and 6 (inclusive) and sets it as the value of the die
     *
     * @author Kai de Gast
     */
    public synchronized void roll() {
            this.setValue(ThreadLocalRandom.current().nextInt(1, 7));
    }

    /**
     * This method returns a String representing the die
     *
     * @author Kai de Gast
     * @return "[x]" colored as the die, where x is the value
     */
    public synchronized String toString() {
        return ansi().fg(this.color).a("[" + this.value + "]").reset().toString();
    }
}
