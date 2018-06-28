package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard9 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    public PublicObjectiveCard9() {
        super("Diagonali Colorate",
                "Numero di dadi dello stesso colore diagonalmente adiacenti");
    }

    /**
     * This method is basically a copy of <code>WindowPattern.getCell(int, int)</code>, but we wrote it because we
     * didn't want to involve the whole WindowPattern class just for this.
     *
     * @author Fabio Codiglioni
     * @param grid the grid to get the die from
     * @param i the row coordinate of the cell
     * @param j the column coordinate of the cell
     * @return the die placed at grid[i][j]
     * @see it.polimi.ingsw.model.patterncards.WindowPattern#getCell(int, int)
     */
    private Die getDie(Cell[] grid, int i, int j) {
        return grid[i*5 + j].getPlacedDie();
    }

    /**
     * This method computes the Victory Points gained from the card
     *
     * @author Fabio Codiglioni
     * @param grid the grid of the player you want to compute Victory Points for.
     * @return the Victory Points gained from the card.
     */
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
