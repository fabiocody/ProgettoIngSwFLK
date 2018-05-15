package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;


public class ValueConstraint extends Constraint {

    public ValueConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (grid[position].getCellValue() == null || grid[position].getCellValue() == d.getValue()) && super.checkConstraint(grid, position, d);
    }

}
