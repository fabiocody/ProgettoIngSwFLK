package it.polimi.ingsw.model.objectivecards;

import java.util.NoSuchElementException;


/**
 * This exception is thrown when a Java Reflection Exception is thrown.
 *
 * @author Fabio Codiglioni
 * @see ObjectiveCardsGenerator
 */
public class NoSuchObjectiveCardException extends NoSuchElementException {

    NoSuchObjectiveCardException(String message) {
        super(message);
    }

}
