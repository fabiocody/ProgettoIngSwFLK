package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard2 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    PublicObjectiveCard2() {
        super("Colori diversi - Colonna",
                "Colonne senza colori ripetuti",
                5);
    }

    /**
     * This method computes the Victory Points gained from the card
     *
     * @author Fabio Codiglioni
     * @param grid the grid of the player you want to compute Victory Points for.
     * @return the Victory Points gained from the card.
     */
    public int calcScore(Cell[] grid) {
        int score = 0;
        for (int j = 0; j < 5; j++) {
            List<Cell> column = Arrays.asList(grid[j], grid[5+j], grid[10+j], grid[15+j]);
            int numberOfDistinctColors = column.stream()
                    .filter(c -> c.getPlacedDie() != null)
                    .map(c -> c.getPlacedDie().getColor())
                    .distinct()
                    .collect(Collectors.toList())
                    .size();
            if (numberOfDistinctColors == 4) score += this.getVictoryPoints();
        }
        return score;
    }

}
