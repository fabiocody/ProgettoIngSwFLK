package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.util.Constants;


public class BorderConstraint extends Constraint {

    public BorderConstraint(PlacementConstraint p) {
        super(p);
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return (position % Constants.WINDOW_PATTERN_COLUMN_NUMBER == 0 || position % Constants.WINDOW_PATTERN_COLUMN_NUMBER == 4 ||
                position >= 0 && position < Constants.WINDOW_PATTERN_COLUMN_NUMBER ||
                position >= Constants.WINDOW_PATTERN_COLUMN_NUMBER *Constants.WINDOW_PATTERN_ROW_NUMBER - Constants.WINDOW_PATTERN_COLUMN_NUMBER
                && position < Constants.WINDOW_PATTERN_COLUMN_NUMBER *Constants.WINDOW_PATTERN_ROW_NUMBER)
                && super.checkConstraint(grid, position, d);
    }

}
