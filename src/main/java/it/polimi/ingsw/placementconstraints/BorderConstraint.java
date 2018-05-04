package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

public class BorderConstraint extends Constraint {

    public BorderConstraint(PlacementConstraint p) {
        super(p);
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return position % 5 == 0 || position % 5 == 4 || position >= 0 && position < 5 || position >= 15 && position < 20;
    }
}
