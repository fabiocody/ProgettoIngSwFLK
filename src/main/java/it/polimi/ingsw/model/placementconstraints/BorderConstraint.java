package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;


public class BorderConstraint extends Constraint {

    public BorderConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (position % 5 == 0 || position % 5 == 4 || position >= 0 && position < 5 || position >= 15 && position < 20)  && super.checkConstraint(grid, position, d);
    }

}
