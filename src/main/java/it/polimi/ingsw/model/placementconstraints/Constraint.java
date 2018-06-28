package it.polimi.ingsw.model.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;


/**
 * This class describes the structure and implements the methods of a Constraint.
 * @author  Luca dell'Oglio
 */
public class Constraint implements PlacementConstraint {

    private PlacementConstraint addedConstraint;

    /**
     * @param   constraint the constraint to be added
     * @author  Team
     */
    Constraint(PlacementConstraint constraint){
        this.addedConstraint = constraint;
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return addedConstraint.checkConstraint(grid, position, d);
    }

    /**
     * @param   position a position on the <code>grid</code> of a <code>WindowPattern</code>
     * @return  the valid placement indexes for <code>position</code>
     * @author  Team
     */
    static List<Integer> validPlacementPositions(int position) {
        List<Integer> list = new ArrayList<>(Arrays.asList(  position - 6, position - 5, position - 4,
                                    position - 1,               position + 1,
                                    position + 4, position + 5, position + 6));
        if (position % 5 == 0) {                                                    //cell on the left border
            list.remove(Integer.valueOf(position - 6));
            list.remove(Integer.valueOf(position - 1));
            list.remove(Integer.valueOf(position + 4));
        }
        if (position % 5 == 4) {                                                    //cell on the right border
            list.remove(Integer.valueOf(position - 4));
            list.remove(Integer.valueOf(position + 1));
            list.remove(Integer.valueOf(position + 6));
        }
        if (position >= 0 && position < 5) {                                        //cell on the left border
            list.remove(Integer.valueOf(position - 6));
            list.remove(Integer.valueOf(position - 5));
            list.remove(Integer.valueOf(position - 4));
        }
        if (position >= 15 && position < 20) {                                      //cell on the left border
            list.remove(Integer.valueOf(position + 4));
            list.remove(Integer.valueOf(position + 5));
            list.remove(Integer.valueOf(position + 6));
        }
        return list;
    }

    /**
     * @param   position a position on the <code>grid</code> of a <code>WindowPattern</code>
     * @return  the indexes of the orthogonally adjacent cells to the cell in <code>position</code>
     * @author  Team
     */
    static List<Integer> validOrthogonalPositions(int position) {
        List<Integer> list = validPlacementPositions(position);
        list.remove(Integer.valueOf(position - 6));
        list.remove(Integer.valueOf(position - 4));
        list.remove(Integer.valueOf(position + 4));
        list.remove(Integer.valueOf(position + 6));
        return list;
    }

}