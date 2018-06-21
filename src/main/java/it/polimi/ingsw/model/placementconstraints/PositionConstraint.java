package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import java.util.List;

/**
 * This class describes the constraint of having to place a die adjacent to an already placed die.
 * @author  Team
 */

public class PositionConstraint extends Constraint {

    public PositionConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        List<Integer> validPositions = validPositions(position);
        Long result = validPositions.stream()
                .filter(c -> grid[c].getPlacedDie() != null)
                .count();
        return result > 0 && super.checkConstraint(grid, position, d);
    }

}
