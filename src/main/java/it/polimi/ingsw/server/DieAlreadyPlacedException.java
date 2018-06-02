package it.polimi.ingsw.server;

/**
 * This exception is thrown when a player tries to place two roundTrack in the same turn.
 *
 * @author Luca dell'Oglio, Kai de Gast
 */

public class DieAlreadyPlacedException extends Exception{
    public DieAlreadyPlacedException(String message) {
        super(message);
    }
}
