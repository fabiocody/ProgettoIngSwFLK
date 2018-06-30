package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard3 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    PublicObjectiveCard3() {
        super("Sfumature diverse - Riga",
                "Righe senza sfumature ripetute",
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
        for (int i = 0; i < 4; i++) {
            int numberOfDistinctValues = Arrays.stream(Arrays.copyOfRange(grid, i * 5, (i + 1) * 5))
                    .filter(c -> c.getPlacedDie() != null)
                    .map(c -> c.getPlacedDie().getValue())
                    .distinct()
                    .collect(Collectors.toList())
                    .size();
            if (numberOfDistinctValues == 5) score += this.getVictoryPoints();
        }
        return score;
    }

}
