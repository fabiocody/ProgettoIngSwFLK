package it.polimi.ingsw.util;


/**
 * This class is used to colorize a string.
 *
 * @author Fabio Codiglioni
 * @see Colors
 */
public class Colorify {

    /**
     * Since this class has only static methods, the constructor should never be called.
     *
     * @author Fabio Codiglioni
     */
    private Colorify() {
        throw new IllegalStateException("Cannot instantiate");
    }

    /**
     * @author Fabio Codiglioni
     * @param str the string to colorize
     * @param color the color you want for the string
     * @return the colorized string
     * @see Colors
     */
    public static String colorify(String str, Colors color) {
        return color.escape() + str + Colors.RESET.escape();
    }

}
