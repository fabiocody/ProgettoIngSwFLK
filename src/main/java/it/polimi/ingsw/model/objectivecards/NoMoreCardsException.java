package it.polimi.ingsw.model.objectivecards;


/**
 * This exception is thrown when the generator has already generated/dealt all the cards.
 *
 * @author Fabio Codiglioni
 * @see ObjectiveCardsGenerator
 */
public class NoMoreCardsException extends RuntimeException {

    NoMoreCardsException() {
        super();
    }

}
