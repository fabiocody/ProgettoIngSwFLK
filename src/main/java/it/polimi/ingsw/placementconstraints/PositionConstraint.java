package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PositionConstraint extends Constraint {

    public PositionConstraint(PlacementConstraint p) {
        super(p);
    }

    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        List<Integer> validPositions = validPositions(position);
        Long result = validPositions.stream()
                .filter(c -> grid[c].getPlacedDie() != null)
                .count();
        return result > 0 && super.checkConstraint(grid,position,d);
    }
}
