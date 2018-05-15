package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;


public class EmptyConstraint implements PlacementConstraint {

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return grid[position].getPlacedDie() == null;
    }

}
