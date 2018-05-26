package it.polimi.ingsw.util;

import org.fusesource.jansi.Ansi;

import java.util.concurrent.ThreadLocalRandom;


/**
 * This Enum is used to represent the possible colors of dice, and for color printing.
 *
 * @author Team
 */
public enum Colors {

    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    DEFAULT("\u001B[0m");

    private String ansiEscape;

    Colors(String ansiCode) {this.ansiEscape = ansiCode;}

    /**
     * @return the ANSI escape code of the color.
     */
    public String escape() {return this.ansiEscape;}

    public Ansi.Color getJAnsiColor() {
        try {
            return Ansi.Color.valueOf(this.toString());
        } catch (IllegalArgumentException e) {
            return Ansi.Color.valueOf("MAGENTA");
        }
    }

    /**
     * @return a random value from this Enum, <code>RESET</code> excluded.
     */
    public static Colors getRandomColor(){
        return values()[ThreadLocalRandom.current().nextInt(values().length-1)];
    }
}
