package it.polimi.ingsw.shared.util;

public class NotificationMessages {

    public static final String TURN_MANAGEMENT = "turnManagement";
    public static final String ROUND_TRACK = "roundTrack";
    public static final String ROUND_INCREMENTED = "roundIncremented";
    public static final String GAME_OVER = "gameOver";
    public static final String GAME_INTERRUPTED = "gameInterrupted";
    public static final String WAITING_ROOM = "waitingRoom";
    public static final String TURN_MANAGER = "turnManager";
    public static final String CANCEL_TOOL_CARD = "cancelToolCard";

    private NotificationMessages() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate");
    }

}
