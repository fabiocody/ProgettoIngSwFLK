package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;


/**
 * This exception is thrown if the JsonObject passed to <code>ToolCard.effect</code> contains any
 * invalid values.
 *
 * @author Fabio Codiglioni
 * @see ToolCard#effect(JsonObject)
 */
public class InvalidEffectArgumentException extends Exception {

    InvalidEffectArgumentException() {
        super();
    }

    InvalidEffectArgumentException(String message) {
        super(message);
    }

}
