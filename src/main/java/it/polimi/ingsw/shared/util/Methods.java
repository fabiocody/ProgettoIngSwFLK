package it.polimi.ingsw.shared.util;

import java.util.NoSuchElementException;


/**
 * This Enum defines all the possible methods that can be called by the client to the game and vice versa.
 *
 * @author Team
 */
public enum Methods {

    ADD_PLAYER("addPlayer"),
    UPDATE_WAITING_PLAYERS("updateWaitingPlayers"),
    WR_TIMER_TICK("wrTimerTick"),
    GAME_SETUP("gameSetup"),
    GAME_TIMER_TICK("gameTimerTick"),
    CHOOSE_PATTERN("choosePattern"),
    TURN_MANAGEMENT("turnManagement"),
    PLAYERS("players"),
    NEXT_TURN("nextTurn"),
    FINAL_SCORES("finalScores"),
    PUBLIC_OBJECTIVE_CARDS("publicObjectiveCards"),
    TOOL_CARDS("toolCards"),
    REQUIRED_DATA("requiredData"),
    FAVOR_TOKENS("favorTokens"),
    WINDOW_PATTERNS("windowPatterns"),
    ROUND_TRACK("roundTrack"),
    DRAFT_POOL("draftPool"),
    PLACE_DIE("placeDie"),
    USE_TOOL_CARD("useToolCard"),
    CANCEL_TOOL_CARD_USAGE("cancelToolCardUsage"),
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
    public static Methods fromString(String string) {
        for (Methods m : values()) {
            if (m.getString().equals(string))
                return m;
        }
        throw new NoSuchElementException(string);
    }

}
