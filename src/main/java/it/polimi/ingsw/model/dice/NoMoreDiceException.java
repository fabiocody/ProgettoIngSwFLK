package it.polimi.ingsw.model.dice;

/**
 * This exception is thrown when there are no more
 * dice left in the dice bag
 *
 * @author Kai de Gast
 */
class NoMoreDiceException extends RuntimeException {

    NoMoreDiceException(String message) {
        super(message);
    }

}
