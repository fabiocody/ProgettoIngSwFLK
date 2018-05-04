package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.Colorify;
import it.polimi.ingsw.util.Colors;
import java.util.concurrent.ThreadLocalRandom;

public class Die {
    private int value;
    private Colors color;

    public Die(Colors color, int value) {
        this.color = color;
        this.value = value;
    }

    public Die(Colors color) {
        this.color = color;
        this.roll();
    }

    public Die() {}

    public int getValue() {
        return this.value;
    }

    public Colors getColor() {
        return this.color;
    }

    public void setValue(int newVal) {
        this.value = newVal;
    }

    public void roll() {
        this.setValue(ThreadLocalRandom.current().nextInt(1,7));
    }

    public String toString() {
        return Colorify.colorify("[" + this.value + "]" , this.color);
    }
}
