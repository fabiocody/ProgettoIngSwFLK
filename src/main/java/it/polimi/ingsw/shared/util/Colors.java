package it.polimi.ingsw.shared.util;

import javafx.scene.paint.Color;
import org.fusesource.jansi.Ansi;

import java.util.concurrent.ThreadLocalRandom;


/**
 * This Enum is used to represent the possible colors of dice, and for color printing.
 *
 * @author Team
 */
public enum Colors {

    RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE;

    public Ansi.Color getJAnsiColor() {
        try {
            return Ansi.Color.valueOf(this.toString());
        } catch (IllegalArgumentException e) {
            return Ansi.Color.valueOf("MAGENTA");
        }
    }

    public Color getJavaFXColor() {
        return Color.valueOf(this.toString());
    }

    /**
     * @return a random value from this Enum, <code>RESET</code> excluded.
     */
    public static Colors getRandomColor(){
        return values()[ThreadLocalRandom.current().nextInt(values().length-1)];
    }
}
