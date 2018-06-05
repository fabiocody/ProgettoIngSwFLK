package it.polimi.ingsw.util;


public class JsonFields {

    public static final String PLAYER_ID = "playerID";
    public static final String METHOD ="method";
    public static final String NICKNAME ="nickname";
    public static final String ARG = "arg";
    public static final String LOGGED = "logged";
    public static final String PLAYERS = "players";
    public static final String RESULT = "result";
    public static final String TICK = "tick";
    public static final String PRIVATE_OBJECTIVE_CARD = "privateObjectiveCard";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String VICTORY_POINTS = "victoryPoints";
    public static final String WINDOW_PATTERNS = "windowPatterns";
    public static final String DIFFICULTY = "difficulty";
    public static final String GRID = "grid";
    public static final String COLOR = "color";
    public static final String VALUE = "value";
    public static final String DIE = "die";
    public static final String CLI_STRING = "cliString";
    public static final String PATTERN_INDEX = "patternIndex";
    public static final String ACTIVE_PLAYER = "activePlayer";
    public static final String CURRENT_ROUND = "currentRound";
    public static final String GAME_OVER = "gameOver";
    public static final String FINAL_SCORES = "finalScores";
    public static final String PUBLIC_OBJECTIVE_CARDS = "publicObjectiveCards";
    public static final String TOOL_CARDS = "toolCards";
    public static final String USE_TOOL_CARD = "useToolCard";
    public static final String USED = "used";
    public static final String FAVOR_TOKENS = "favorTokens";
    public static final String DICE = "dice";
    public static final String DRAFT_POOL_INDEX = "draftPoolIndex";
    public static final String CARD_INDEX = "cardIndex";
    public static final String DATA = "data";
    public static final String ROUND_TRACK_INDEX = "roundTrackIndex";
    public static final String ROUND_TRACK = "roundTrack";
    public static final String DELTA = "delta";
    public static final String NEW_VALUE = "newValue";
    public static final String FROM_CELL_X = "fromCellX";
    public static final String FROM_CELL_Y = "fromCellY";
    public static final String TO_CELL_X = "toCellX";
    public static final String TO_CELL_Y = "toCellY";
    public static final String PLAYER = "player";
    public static final String PUT_AWAY = "putAway";
    public static final String CONTINUE = "continue";

    private JsonFields(String string) throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate");
    }

}
