package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import java.util.List;

/**
 * This class describes the constraint of not being able to place a die orthogonally adjacent to a die of the same color or value.
 * @author  Team
 */

public class OrthogonalConstraint extends Constraint {

    public OrthogonalConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        List<Integer> validPositions = validOrthogonalPositions(position);
        Long result = validPositions.stream()
                .filter(x -> grid[x].getPlacedDie() != null)
                .map(x -> grid[x].getPlacedDie())
                .filter(die -> die.getColor() == d.getColor() || die.getValue() == d.getValue())
                .count();
        return result == 0 && super.checkConstraint(grid, position, d);
    }

}
