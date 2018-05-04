package it.polimi.ingsw.util;


public class Colorify {

    public static String colorify(String str, Colors color) {
        return color.escape() + str + Colors.RESET.escape();
    }

}
