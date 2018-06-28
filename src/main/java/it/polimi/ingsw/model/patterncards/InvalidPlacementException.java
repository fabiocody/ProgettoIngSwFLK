package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;


/**
 * This exception is thrown when trying to place a die in an invalid position, violating <code>PlacementConstraints</code>
 * @author  Luca dell'Oglio
 * @see     PlacementConstraint
 * @see     WindowPattern#placeDie(Die, int)
 * @see     WindowPattern#moveDie(int, int, PlacementConstraint)
 */
public class InvalidPlacementException extends RuntimeException{

    public InvalidPlacementException() {
        super();
    }

    public InvalidPlacementException(String message) {
        super(message);
    }

}
