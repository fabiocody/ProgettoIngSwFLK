package it.polimi.ingsw.shared.util;

import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import static org.fusesource.jansi.Ansi.ansi;


public class Logger {

    private static boolean debug = false;

    private Logger() {
        throw new IllegalStateException("Cannot instantiate");
    }

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
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
        if (debug) println("[DEBUG " + LocalDateTime.now() + "] " + message);
    }

    public static void debugInput(JsonObject input) {
        debug("INPUT " + input.toString());
    }

    public static void debugPayload(JsonObject payload) {
        debug("PAYLOAD " + payload.toString());
    }

    public static void error(String message) {
        String output;
        if (debug) output = "[ERROR " + LocalDateTime.now() + "] " + message;
        else output = "[ERROR] " + message;
        System.err.println(ansi().fgRed().a(output).reset());
    }

    public static void conditionalError(String message) {
        if (debug) error(message);
    }

    public static void connectionLost(String nickname) {
        error("Connection lost (" + nickname + ")");
    }

    public static void connectionLost() {
        error("Connection lost");
    }

    public static void printStackTrace(Throwable e) {
        e.printStackTrace();
    }

    public static void printStackTraceConditionally(Throwable e) {
        if (debug && e != null) e.printStackTrace();
    }

}
