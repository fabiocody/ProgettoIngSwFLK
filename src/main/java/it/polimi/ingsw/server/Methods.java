package it.polimi.ingsw.server;

import java.util.NoSuchElementException;


/**
 * This Enum defines all the possible methods that can be called by the client to the server and vice versa.
 *
 * @author Team
 */
public enum Methods {

    ADD_PLAYER("addPlayer"),
    UPDATE_WAITING_PLAYERS("updateWaitingPlayers"),
    SUBSCRIBE_TO_WR_TIMER("subscribeToWRTimer"),
    WR_TIMER_TICK("wrTimerTick"),
    GAME_SETUP("gameSetup"),
    SUBSCRIBE_TO_GAME_TIMER("subscribeToGameTimer"),
    GAME_TIMER_TICK("gameTimerTick"),
    CHOOSE_PATTERN("choosePattern"),
    GAME_STARTED("gameStarted"),
    PLAYERS("players"),
    NEXT_TURN("nextTurn"),
    FINAL_SCORES("finalScores"),
    PUBLIC_OBJECTIVE_CARDS("publicObjectiveCards"),
    TOOL_CARDS("toolCards"),
    FAVOR_TOKENS("favorTokens"),
    WINDOW_PATTERNS("windowPatterns"),
    ROUND_TRACK_DICE("roundTrackDice"),
    DRAFT_POOL("draftPool"),
    PLACE_DIE("placeDie"),
    USE_TOOL_CARD("useToolCard"),
    PROBE("probe");


    private String string;

    Methods(String string) {
        this.string = string;
    }

    /**
     * @author Team
     * @return the String used to textually represent the method.
     */
    public String getString() {
        return this.string;
    }

    /**
     * @author Fabio Codiglioni
     * @param string the String representation of the Method.
     * @return the Enum value corresponding to the specified String.
     * @throws NoSuchElementException thrown when there's no Method represented by the given String.
     */
    public static Methods getAsMethods(String string) {
        for (Methods m : values()) {
            if (m.getString().equals(string))
                return m;
        }
        throw new NoSuchElementException(string);
    }

}
