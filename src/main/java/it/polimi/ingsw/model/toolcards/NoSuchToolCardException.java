package it.polimi.ingsw.model.toolcards;

import java.util.NoSuchElementException;


/**
 * This exception is thrown when a Java Reflection Exception is thrown.
 *
 * @author Fabio Codiglioni
 * @see ToolCardsGenerator
 */
class NoSuchToolCardException extends NoSuchElementException {

    NoSuchToolCardException(String message) {
        super(message);
    }

}
