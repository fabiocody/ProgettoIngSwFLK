package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

public class OrthogonalConstraint extends Constraint {

    public OrthogonalConstraint(PlacementConstraint p) {
        super(p);
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        //TODO
        return true;
    }
}
