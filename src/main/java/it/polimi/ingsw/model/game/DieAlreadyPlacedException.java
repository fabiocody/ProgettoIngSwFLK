package it.polimi.ingsw.model.game;

/**
 * This exception is thrown when a player tries to place two roundTrack in the same turn.
 *
 * @author Luca dell'Oglio, Kai de Gast
 */

public class DieAlreadyPlacedException extends RuntimeException{

    DieAlreadyPlacedException() {
        super();
    }

}
