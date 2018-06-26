package it.polimi.ingsw.shared.util;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Cannot instantiate");
    }

    public static final int INDEX_CONSTANT = 42;
    public static final int MAX_NICKNAME_LENGTH = 21;
    public static final int MAX_NUMBER_OF_PLAYERS = 4;
    public static final int MAX_NUMBER_OF_SAME_COLOR_DICE = 18;
    public static final int NUMBER_OF_PUB_OBJ_CARDS = 3;
    public static final int PATTERN_CARDS_FOR_EACH_PLAYER = 2;
    public static final int NUMBER_OF_PATTERN_COLUMNS = 5;
    public static final int NUMBER_OF_PATTERN_ROWS = 4;
    public static final int NUMBER_OF_PATTERNS = 24;
    public static final int TOOL_CARD_NUMBER = 3;
    public static final int NUMBER_OF_ROUNDS = 2;
    public static final String EXIT_MESSAGE = "Premi 0 per annullare.";
    public static final int DEFAULT_PORT = 42000;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final int DEFAULT_WR_TIMEOUT = 30;
    public static final int DEFAULT_GAME_TIMEOUT = 90;
    public static final int EXIT_STATUS = 0;
    public static final int EXIT_ERROR = 1;
    public static final long PROBE_TIMEOUT = 300;
    public static final String SERVER_RMI_NAME = "SagradaServer";
    public static final String SERVER_USAGE_STRING = "usage: sagradaserver host [--debug] [--port PORT] [--wr-timeout WR-TIMEOUT] [--game-timeout GAME-TIMEOUT]";
    public static final String CLIENT_USAGE_STRING = "usage: sagradaclient --host HOST [--debug] [--port PORT] [--connection socket|rmi] [--interface cli|gui]";
}
