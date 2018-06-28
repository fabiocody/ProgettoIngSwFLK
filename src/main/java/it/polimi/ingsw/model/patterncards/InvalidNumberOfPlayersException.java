package it.polimi.ingsw.model.patterncards;


/**
 * This exception is thrown when the parameter passed to the constructor <code>PatternCardsGenerator</code> is not equal
 * to 2,3 or 4
 *
 * @author  Luca dell'Oglio
 * @see     PatternCardsGenerator#PatternCardsGenerator(int)
 */
class InvalidNumberOfPlayersException extends RuntimeException {

    InvalidNumberOfPlayersException() {
        super();
    }

}
