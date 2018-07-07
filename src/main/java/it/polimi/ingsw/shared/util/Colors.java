package it.polimi.ingsw.shared.util;

import javafx.scene.paint.Color;
import org.fusesource.jansi.Ansi;

import java.util.concurrent.ThreadLocalRandom;


/**
 * This Enum is used to represent the possible colors of dice.
 *
 * @author Team
 */
public enum Colors {

    RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE;

    /**
     * @return the Ansi.Color corresponding to the Colors instance
     */
    public Ansi.Color getJAnsiColor() {
        if (this == PURPLE)
            return Ansi.Color.MAGENTA;
        else if (this == BLUE)
            return Ansi.Color.CYAN;
        return Ansi.Color.valueOf(toString());
    }

    /**
     * @return the JavaFX Color corresponding to the Colors instance
     */
    public Color getJavaFXColor() {
        if (this == YELLOW)
            return Color.GOLD;
        else if (this == RED)
            return Color.CRIMSON;
        else if (this == BLUE)
            return Color.DARKTURQUOISE;
        return Color.valueOf(this.toString());
    }

    /**
     * @return a random value from this Enum.
     */
    public static Colors getRandomColor(){
        return values()[ThreadLocalRandom.current().nextInt(values().length-1)];
    }
}
