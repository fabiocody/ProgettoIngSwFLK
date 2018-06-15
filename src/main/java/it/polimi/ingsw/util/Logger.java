package it.polimi.ingsw.util;

import java.time.LocalDateTime;

public class Logger {

    private static boolean debugActive = false;

    public static boolean isDebugActive() {
        return debugActive;
    }

    public static void setDebugActive(boolean debugActive) {
        Logger.debugActive = debugActive;
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void println() {
        println("");
    }

    public static void debug(String message) {
        if (isDebugActive()) println("[DEBUG " + LocalDateTime.now() + "] " + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }
}
