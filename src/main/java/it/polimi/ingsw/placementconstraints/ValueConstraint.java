package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;


public class ValueConstraint extends Constraint {

    public ValueConstraint(PlacementConstraint p) {
        super(p);
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (grid[position].getCellValue() == null || grid[position].getCellValue() == d.getValue()) && super.checkConstraint(grid, position, d);
    }

}
