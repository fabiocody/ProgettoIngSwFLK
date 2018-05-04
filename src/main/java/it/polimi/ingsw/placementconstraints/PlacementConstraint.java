package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

public abstract class PlacementConstraint {

    public abstract boolean checkConstraint(Cell[] grid, int position, Die d);

    public static PlacementConstraint initialConstraint() {
        return new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
    }

    public static PlacementConstraint standardConstraint() {
        return new PositionConstraint(new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint()))));
    }

}
