package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;


public interface PlacementConstraint {

    boolean checkConstraint(Cell[] grid, int position, Die d);

    static PlacementConstraint initialConstraint() {
        return new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
    }

    static PlacementConstraint standardConstraint() {
        return new PositionConstraint(new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint()))));
    }

}
