package it.polimi.ingsw.util;

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
    RESET("\u001B[0m");

    private String ansiCode;

    Colors(String ansiCode) {this.ansiCode = ansiCode;}

    /**
     * @return the ANSI escape code of the color.
     */
    public String escape() {return this.ansiCode;}

    /**
     * @return a random value from this Enum, <code>RESET</code> excluded.
     */
    public static Colors getRandomColor(){
        return values()[ThreadLocalRandom.current().nextInt(values().length-1)];
    }
}
