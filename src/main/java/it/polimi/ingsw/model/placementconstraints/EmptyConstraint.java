package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;

/**
 * This class describes the constraint of having to place the die on an empty cell
 * @author  Team
 */

public class EmptyConstraint implements PlacementConstraint {

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return grid[position].getPlacedDie() == null;
    }

}
