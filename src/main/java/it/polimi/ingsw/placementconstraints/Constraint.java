package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

public class Constraint implements PlacementConstraint {

    private PlacementConstraint addedConstraint;

    public Constraint(PlacementConstraint p){
        this.addedConstraint = p;
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return addedConstraint.checkConstraint(grid, position, d);
    }

}