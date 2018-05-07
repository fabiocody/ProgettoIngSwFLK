package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.Colorify;
import it.polimi.ingsw.util.Colors;
import java.util.concurrent.ThreadLocalRandom;

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

    public synchronized int getValue() {
            return this.value;
    }

    public Colors getColor() {
            return this.color;
    }

    public synchronized void setValue(int newVal) {
            this.value = newVal;
    }

    public synchronized void roll() {
            this.setValue(ThreadLocalRandom.current().nextInt(1, 7));
    }

    public synchronized String toString() {
        return Colorify.colorify("[" + this.value + "]" , this.color);
    }
}
