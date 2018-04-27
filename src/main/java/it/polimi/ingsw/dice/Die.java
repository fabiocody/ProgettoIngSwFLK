package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.Colors;
import java.util.concurrent.ThreadLocalRandom;

public class Die {
    int value;
    Colors color;

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
        this.value = ThreadLocalRandom.current().nextInt(1,7);
    }
}
