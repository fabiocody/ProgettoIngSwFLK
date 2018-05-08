package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Constraint implements PlacementConstraint {

    private PlacementConstraint addedConstraint;

    public Constraint(PlacementConstraint p){
        this.addedConstraint = p;
    }

    @Override
    public boolean checkConstraint(Cell[] grid, int position, Die d) {
        return addedConstraint.checkConstraint(grid, position, d);
    }

    public static List<Integer> validPositions(int position){
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(  position - 6, position - 5, position - 4,
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

    public static List<Integer> validOrthogonalPositions(int position) {
        List<Integer> list = validPositions(position);
        list.remove(Integer.valueOf(position - 6));
        list.remove(Integer.valueOf(position - 4));
        list.remove(Integer.valueOf(position + 4));
        list.remove(Integer.valueOf(position + 6));
        return list;
    }

}