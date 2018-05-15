package it.polimi.ingsw.server;


import java.util.NoSuchElementException;

public enum Methods {

    ADD_PLAYER("addPlayer"),
    UPDATE_WAITING_PLAYERS("updateWaitingPlayers"),
    SUBSCRIBE_TO_WR_TIMER("subscribeToWRTimer"),
    WR_TIMER_TICK("wrTimerTick"),
    GAME_STARTED("gameStarted"),
    SUBSCRIBE_TO_GAME_TIMER("subscribeToGameTimer"),
    GAME_TIMER_TICK("gameTimerTick"),
    CHOOSE_PATTERN("choosePattern"),
    PLAYERS("players"),
    NEXT_TURN("nextTurn"),
    FINAL_SCORES("finalScores"),
    PUBLIC_OBJECTIVE_CARDS("publicObjectiveCards"),
    TOOL_CARDS("toolCards"),
    FAVOR_TOKENS("favorTokens"),
    WINDOW_PATTERN("windowPattern"),
    ROUND_TRACK_DICE("roundTrackDice"),
    DRAFT_POOL("draftPool"),
    PLACE_DIE("placeDie"),
    USE_TOOL_CARD("useToolCard");


    private String string;

    private Methods(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public static Methods getAsMethods(String string) {
        for (Methods m : values()) {
            if (m.getString().equals(string))
                return m;
        }
        throw new NoSuchElementException(string);
    }

}
