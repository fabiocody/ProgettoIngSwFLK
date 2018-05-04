package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;


public class EmptyConstraint implements PlacementConstraint {

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return grid[position].getPlacedDie() == null;
    }

}
