package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;


public class ColorConstraint extends Constraint {

    public ColorConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (grid[position].getCellColor() == null || grid[position].getCellColor() == d.getColor()) && super.checkConstraint(grid, position, d);
    }

}

