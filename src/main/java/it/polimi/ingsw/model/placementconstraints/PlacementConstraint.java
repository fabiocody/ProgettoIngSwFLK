package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;

/**
 * This interface describes the methods of a PlacementConstraint.
 * @author  Team
 */

public interface PlacementConstraint {

    /**
     * @param   grid the <code>WindowPattern</code> grid where <code>d</code> will be placed
     * @param   position the index where <code>d</code> will be placed
     * @param   d the <code>Die</code> to be placed
     * @return  if the placement checks the <code>Constraint</code>
     * @author  Team
     */

    boolean checkConstraint(Cell[] grid, int position, Die d);

    /**
     * @return  the <code>Constraint</code> to be checked when placing a <code>Die</code> on an empty grid of a <code>WindowPattern</code>
     * @author  Team
     */

    static PlacementConstraint initialConstraint() {
        return new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
    }

    /**
     * @return  the <code>Constraint</code> to be checked when placing a <code>Die</code> on a not empty grid of a <code>WindowPattern</code>
     * @author  Team
     */

    static PlacementConstraint standardConstraint() {
        return new PositionConstraint(new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint()))));
    }

}
