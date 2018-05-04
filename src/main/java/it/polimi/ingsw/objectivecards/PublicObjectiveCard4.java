package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


public class PublicObjectiveCard4 extends ObjectiveCard {

    public PublicObjectiveCard4() {
        super("Sfumature diverse - Colonna",
                "Colonne senza sfumature ripetute",
                6);
    }

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
