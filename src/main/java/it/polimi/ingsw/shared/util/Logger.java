package it.polimi.ingsw.shared.util;

import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import static org.fusesource.jansi.Ansi.ansi;


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
        println("[" + LocalDateTime.now() + "] " + message);
    }

    public static void debug(String message) {
        if (isDebugActive()) println("[DEBUG " + LocalDateTime.now() + "] " + message);
    }

    public static void debugPayload(JsonObject payload) {
        debug("PAYLOAD " + payload.toString());
    }

    public static void error(String message) {
        String output;
        if (isDebugActive())
            output = "[ERROR " + LocalDateTime.now() + "] " + message;
        else
            output = "[ERROR] " + message;
        System.err.println(ansi().fgRed().a(output).reset());
    }

    public static void connectionLost(String nickname) {
        error("Connection lost (" + nickname + ")");
    }

    public static void connectionLost() {
        error("Connection lost");
    }

}
