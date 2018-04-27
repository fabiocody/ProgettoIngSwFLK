package it.polimi.ingsw.util;


public class PPrint {

    private static void printColored(String str, Colors color) {
        System.out.println(color.escape() + str + Colors.RESET.escape());
    }

    static void printRed(String str) {
        printColored(str, Colors.RED);
    }

    static void printGreen(String str) {
        printColored(str, Colors.GREEN);
    }

    static void printYellow(String str) {
        printColored(str, Colors.YELLOW);
    }

    static void printBlue(String str) {
        printColored(str, Colors.BLUE);
    }

    static void printPurple(String str) {
        printColored(str, Colors.PURPLE);
    }

}
