package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard1 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    PublicObjectiveCard1() {
        super("Colori diversi - Riga",
                "Righe senza colori ripetuti",
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
        for (int i = 0; i < 4; i++) {
            int numberOfDistinctColors = Arrays.stream(Arrays.copyOfRange(grid, i * 5, (i + 1) * 5))
                    .filter(c -> c.getPlacedDie() != null)
                    .map(c -> c.getPlacedDie().getColor())
                    .distinct()
                    .collect(Collectors.toList())
                    .size();
            if (numberOfDistinctColors == 5) score += this.getVictoryPoints();
        }
        return score;
    }

}
