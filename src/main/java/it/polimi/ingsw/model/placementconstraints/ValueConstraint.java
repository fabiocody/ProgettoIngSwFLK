package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;

/**
 * This class describes the constraint of having to place the die on a cell of the same value or with no value.
 * @author  Team
 */

public class ValueConstraint extends Constraint {

    public ValueConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (grid[position].getCellValue() == null || grid[position].getCellValue() == d.getValue()) && super.checkConstraint(grid, position, d);
    }

}
