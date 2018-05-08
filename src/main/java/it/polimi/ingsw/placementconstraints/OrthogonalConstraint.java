package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import java.util.List;


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
