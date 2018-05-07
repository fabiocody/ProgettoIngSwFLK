package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;


public interface PlacementConstraint {

    boolean checkConstraint(Cell[] grid, int position, Die d);

    static PlacementConstraint initialConstraint() {
        return new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
    }

    static PlacementConstraint standardConstraint() {
        return new PositionConstraint(new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint()))));
    }

}
