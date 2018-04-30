package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

import java.util.List;

public class ColorConstraint extends Constraint {

    public ColorConstraint(PlacementConstraint p) {
        super(p);
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return grid[position].getCellColor() == null || grid[position].getCellColor() == d.getColor();
    }
}

