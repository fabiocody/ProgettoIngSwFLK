package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;


public class PublicObjectiveCard9 extends ObjectiveCard {

    public PublicObjectiveCard9() {
        super("Diagonali Colorate",
                "Numero di dadi dello stesso colore diagonalmente adiacenti");
    }

    private Die getDie(Cell[] grid, int i, int j) {
        return grid[i*5 + j].getPlacedDie();
    }

    public int calcScore(Cell[] grid) {
        Set<Die> diagonals = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (getDie(grid, i, j) != null) {
                    if (i > 0 && j > 0 &&
                            getDie(grid, i-1, j-1) != null &&
                            getDie(grid, i-1, j-1).getColor() == getDie(grid, i, j).getColor()) {
                        diagonals.add(getDie(grid, i, j));
                        diagonals.add(getDie(grid, i-1, j-1));
                    }
                    if (i > 0 && j < 4 &&
                            getDie(grid, i-1, j+1) != null &&
                            getDie(grid, i-1, j+1).getColor() == getDie(grid, i, j).getColor()) {
                        diagonals.add(getDie(grid, i, j));
                        diagonals.add(getDie(grid, i-1, j+1));
                    }
                    if (i < 3 && j > 0 &&
                            getDie(grid, i+1, j-1) != null &&
                            getDie(grid, i+1, j-1).getColor() == getDie(grid, i, j).getColor()) {
                        diagonals.add(getDie(grid, i, j));
                        diagonals.add(getDie(grid, i+1, j-1));
                    }
                    if (i < 3 && j < 3 &&
                            getDie(grid, i+1, j+1) != null &&
                            getDie(grid, i+1, j+1).getColor() == getDie(grid, i, j).getColor()) {
                        diagonals.add(getDie(grid, i, j));
                        diagonals.add(getDie(grid, i+1, j+1));
                    }
                }
            }
        }
        return diagonals.size();
    }

}
