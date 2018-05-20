package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard4 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    public PublicObjectiveCard4() {
        super("Sfumature diverse - Colonna",
                "Colonne senza sfumature ripetute",
                6);
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
            int numberOfDistinctValues = column.stream()
                    .filter(c -> c.getPlacedDie() != null)
                    .map(c -> c.getPlacedDie().getValue())
                    .distinct()
                    .collect(Collectors.toList())
                    .size();
            if (numberOfDistinctValues == 4) score += this.getVictoryPoints();
        }
        return score;
    }

}
