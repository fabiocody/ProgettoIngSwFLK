package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;


/**
 * This exception is thrown when <code>ToolCard.effect</code> has an invalid effect (e.g. when a die
 * cannot be placed/moved, ...)
 *
 * @author Fabio Codiglioni
 * @see ToolCard#effect(JsonObject)
 */
public class InvalidEffectResultException extends Exception {

    InvalidEffectResultException() {
        super();
    }

    InvalidEffectResultException(String message) {
        super(message);
    }

}
