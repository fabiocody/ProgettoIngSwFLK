package it.polimi.ingsw.shared.util;

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

}
