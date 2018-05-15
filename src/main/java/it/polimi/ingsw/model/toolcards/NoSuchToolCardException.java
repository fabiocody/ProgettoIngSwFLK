package it.polimi.ingsw.model.toolcards;

import java.util.NoSuchElementException;


public class NoSuchToolCardException extends NoSuchElementException {

    public NoSuchToolCardException(String message) {
        super(message);
    }

}
