package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.shared.util.Constants;

/**
 * This class describes the constraint of having to place the die on the border of the grid when the grid is empty
 * @author  Team
 */
public class BorderConstraint extends Constraint {

    public BorderConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (position % Constants.NUMBER_OF_PATTERN_COLUMNS == 0 || position % Constants.NUMBER_OF_PATTERN_COLUMNS == 4 ||
                position >= 0 && position < Constants.NUMBER_OF_PATTERN_COLUMNS ||
                position >= Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS - Constants.NUMBER_OF_PATTERN_COLUMNS
                && position < Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS)
                && super.checkConstraint(grid, position, d);
    }

}
