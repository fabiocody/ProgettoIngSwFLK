package it.polimi.ingsw.shared.util;

public class InterfaceMessages {

    private InterfaceMessages() { throw new IllegalStateException("Cannot instantiate"); }

    public static final String DIE_ALREADY_PLACED_IN_THIS_TURN = "hai già piazzato un dado questo turno!";
    public static final String DIE_INVALID_POSITION = "questo dado non può essere piazzato in questa posizione";
    public static final String SUCCESSFUL_DIE_PLACEMENT = "\nIl dado è stato piazzato!";
    public static final String UNSUCCESSFUL_DIE_PLACEMENT = "\nIl dado non è stato piazzato: ";
    public static final String NO_FAVOR_TOKENS_MESSAGE = "\nNon hai abbastanza segnalini favore!";
    public static final String EMPTY_DRAFT_POOL_MESSAGE = "\nLa riserva è vuota!";
    public static final String DIE_UPPER_BOUND = "\nNon puoi cambiare un 6 in 1!";
    public static final String DIE_LOWER_BOUND = "\nNon puoi cambiare un 1 in 6!";

}
