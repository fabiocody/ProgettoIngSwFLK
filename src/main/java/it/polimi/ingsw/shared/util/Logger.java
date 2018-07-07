package it.polimi.ingsw.shared.util;

import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import static org.fusesource.jansi.Ansi.ansi;


/**
 * This class contains some logging methods
 */
public class Logger {

    private static boolean debug = false;

    /**
     * The default constructor should never be called, thus it's private and it throws an exception
     */
    private Logger() {
        throw new IllegalStateException("Cannot instantiate");
    }

    /**
     * @param debug whether the debug mode is active or not
     */
    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    /**
     * This method prints a message without a trailing newline
     *
     * @param message the message to print
     */
    public static void print(String message) {
        System.out.print(message);
    }

    /**
     * This method prints a message with a trailing newline
     *
     * @param message the message to print
     */
    public static void println(String message) {
        System.out.println(message);
    }

    /**
     * This method prints a newline
     */
    public static void println() {
        println("");
    }

    /**
     * This method prints the current time and a message
     *
     * @param message the message to print
     */
    public static void log(String message) {
        println("[" + LocalDateTime.now() + "] " + message);
    }

    /**
     * This method prints the current time and a message only if debug mode is active
     *
     * @param message the message to print
     */
    public static void debug(String message) {
        if (debug) println("[DEBUG " + LocalDateTime.now() + "] " + message);
    }

    /**
     * This method prints an incoming JsonObject only if debug mode is active
     *
     * @param input the JsonObject to print
     */
    public static void debugInput(JsonObject input) {
        debug("INPUT " + input.toString());
    }

    /**
     * This method prints an outgoing JsonObject only if debug mode is active
     *
     * @param payload the JsonObject to print
     */
    public static void debugPayload(JsonObject payload) {
        debug("PAYLOAD " + payload.toString());
    }

    /**
     * This method prints a red-highlighted message, plus the current time if debug mode is active
     *
     * @param message the message to print
     */
    public static void error(String message) {
        String output;
        if (debug) output = "[ERROR " + LocalDateTime.now() + "] " + message;
        else output = "[ERROR] " + message;
        System.err.println(ansi().fgRed().a(output).reset());
    }

    /**
     * This method prints a red-highlighted message only if debug mode is active
     *
     * @param message the message to print
     */
    public static void conditionalError(String message) {
        if (debug) error(message);
    }

    /**
     * This method prints a "Connection lost" error message with the nickname of the Player who has been disconnected
     *
     * @param nickname the nickname of the Player who has been disconnected
     */
    public static void connectionLost(String nickname) {
        error("Connection lost (" + nickname + ")");
    }

    /**
     * This method prints a "Connection lost" error message
     */
    public static void connectionLost() {
        error("Connection lost");
    }

    /**
     * This method calls <code>printStackTrace</code> on the provided <code>Throwable</code>
     *
     * @param e the Throwable
     */
    public static void printStackTrace(Throwable e) {
        e.printStackTrace();
    }

    /**
     * This method calls <code>printStackTrace</code> on the provided <code>Throwable</code> only if debug mode is active
     *
     * @param e the Throwable
     */
    public static void printStackTraceConditionally(Throwable e) {
        if (debug && e != null) e.printStackTrace();
    }

}
