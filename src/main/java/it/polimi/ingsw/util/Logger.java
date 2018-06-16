package it.polimi.ingsw.util;

import com.google.gson.JsonObject;

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

    public static void log(String message) {
        println(LocalDateTime.now() + message);
    }

    public static void debug(String message) {
        if (isDebugActive()) println("[DEBUG " + LocalDateTime.now() + "] " + message);
    }

    public static void debugPayload(JsonObject payload) {
        debug("PAYLOAD " + payload.toString());
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void connectionLost(String nickname) {
        error("Connection lost (" + nickname + ")");
    }

    public static void connectionLost() {
        error("Connection lost");
    }

}
