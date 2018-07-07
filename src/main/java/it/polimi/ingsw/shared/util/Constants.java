package it.polimi.ingsw.shared.util;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Cannot instantiate");
    }

    public static final int INDEX_CONSTANT = 42;
    public static final int MAX_NICKNAME_LENGTH = 21;
    public static final int MAX_NUMBER_OF_PLAYERS = 4;
    public static final int MAX_NUMBER_OF_SAME_COLOR_DICE = 18;
    public static final int NUMBER_OF_PRI_OBJ_CARDS = 5;
    public static final int NUMBER_OF_PUB_OBJ_CARDS = 10;
    public static final int NUMBER_OF_PUB_OBJ_CARDS_PER_GAME = 3;
    public static final int PATTERN_CARDS_FOR_EACH_PLAYER = 2;
    public static final int NUMBER_OF_PATTERN_COLUMNS = 5;
    public static final int NUMBER_OF_PATTERN_ROWS = 4;
    public static final int NUMBER_OF_PATTERNS = 24;
    public static final int NUMBER_OF_TOOL_CARDS = 12;
    public static final int NUMBER_OF_TOOL_CARDS_PER_GAME = 3;
    public static final int NUMBER_OF_ROUNDS = 10;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 42000;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final int DEFAULT_WR_TIMEOUT = 30;
    public static final int DEFAULT_GAME_TIMEOUT = 90;
    public static final int EXIT_STATUS = 0;
    public static final int EXIT_ERROR = 1;
    public static final long PROBE_TIMEOUT = 3;
    public static final String SERVER_RMI_NAME = "SagradaServer";
    public static final String TOOL_CARD_1_NAME = "Pinza Sgrossatrice";
    public static final String TOOL_CARD_2_NAME = "Pennello per Eglomise";
    public static final String TOOL_CARD_3_NAME = "Alesatore per lamina di rame";
    public static final String TOOL_CARD_4_NAME = "Lathekin";
    public static final String TOOL_CARD_5_NAME = "Taglierina circolare";
    public static final String TOOL_CARD_6_NAME = "Pennello per Pasta Salda";
    public static final String TOOL_CARD_7_NAME = "Martelletto";
    public static final String TOOL_CARD_8_NAME = "Tenaglia a Rotelle";
    public static final String TOOL_CARD_9_NAME = "Riga in Sughero";
    public static final String TOOL_CARD_10_NAME = "Tampone Diamantato";
    public static final String TOOL_CARD_11_NAME = "Diluente per Pasta Salda";
    public static final String TOOL_CARD_12_NAME = "Taglierina Manuale";
    public static final String STRING_SEPARATOR = ", ";

}
