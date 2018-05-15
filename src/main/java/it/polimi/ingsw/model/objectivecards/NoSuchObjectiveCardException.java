package it.polimi.ingsw.model.objectivecards;

import java.util.NoSuchElementException;


public class NoSuchObjectiveCardException extends NoSuchElementException {

    public NoSuchObjectiveCardException(String message) {
        super(message);
    }

}
