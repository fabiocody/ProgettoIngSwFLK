package it.polimi.ingsw.shared.util;

import static it.polimi.ingsw.shared.util.Constants.*;


public class InterfaceMessages {

    private InterfaceMessages() { throw new IllegalStateException("Cannot instantiate"); }

    public static final String DIE_ALREADY_PLACED_IN_THIS_TURN = "hai già piazzato un dado questo turno!";
    public static final String DIE_INVALID_POSITION = "questo dado non può essere piazzato in questa posizione!";
    public static final String SUCCESSFUL_DIE_PLACEMENT = "\nIl dado è stato piazzato!";
    public static final String UNSUCCESSFUL_DIE_PLACEMENT = "\nIl dado non è stato piazzato: ";
    public static final String NO_FAVOR_TOKENS = "non hai abbastanza segnalini favore!";
    public static final String INVALID_MOVE = "questa mossa non è valida!";
    public static final String EMPTY_DRAFT_POOL = "la riserva è vuota";
    public static final String DIE_UPPER_BOUND = "non puoi cambiare un 6 in 1!";
    public static final String DIE_LOWER_BOUND = "non puoi cambiare un 1 in 6!";
    public static final String EMPTY_GRID = "la griglia è vuota";
    public static final String EMPTY_ROUND_TRACK = "il tracciato del round è vuoto";
    public static final String ONE_OR_LESS_DIE_MESSAGE = "la griglia è vuota o c'è un solo dado";
    public static final String UNSUCCESSFUL_TOOL_CARD_USAGE = "\nLa carta strumento non è stata usata: ";
    public static final String FIRST_HALF_OF_ROUND = "non è il tuo secondo turno";
    public static final String DIE_NOT_YET_PLACED_IN_THIS_TURN = "non hai ancora piazzato un dado questo turno";
    public static final String NO_PROPER_COLOR_DIE_ON_ROUND_TRACK = "non ci sono dadi di questo colore nel tracciato del round";

    public static final String WINDOW_TITLE = "Sagrada";
    public static final String LOGIN_FAILED_EMPTY = "Login fallito! I nickname non possono essere vuoti";
    public static final String LOGIN_FAILED_SPACES = "Login fallito! I nickname non possono contenere spazi";
    public static final String LOGIN_FAILED_LENGTH = "Login fallito! I nickname non possono essere più lunghi di " + MAX_NICKNAME_LENGTH + " caratteri";
    public static final String LOGIN_FAILED_USED = "Login fallito! Questo nickname è già in uso";

    public static final String EXIT_MESSAGE = "Sei sicuro di voler uscire?";
    public static final String HOST_PROMPT = "Host: ";
    public static final String PORT_PROMPT = "Porta: ";
    public static final String NICKNAME_PROMPT = "Nickname: ";
    public static final String HOST_PLACEHOLDER = "Inserisci l'indirizzo del server";
    public static final String PORT_PLACEHOLDER = "Inserisci la porta per la socket";
    public static final String NICKNAME_PLACEHOLDER = "Inserisci il tuo nickname";
    public static final String SOCKET = "Socket";
    public static final String RMI = "RMI";
    public static final String CONNECTION_FAILED = "Impossibile connettersi";
    public static final String INVALID_HOST = "Inserisci un host valido";
    public static final String MISSING_DATA = "Dati mancanti e/o non validi";

    public static String patternSelected(int patternIndex) {
        return "Hai scelto il pattern numero " + patternIndex + ".\nPer favore attendi che tutti i giocatori facciano la propria scelta.";
    }

}
