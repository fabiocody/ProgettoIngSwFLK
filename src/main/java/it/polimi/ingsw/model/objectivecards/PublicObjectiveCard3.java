package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.Arrays;
import java.util.stream.Collectors;


public class PublicObjectiveCard3 extends ObjectiveCard {

    public PublicObjectiveCard3() {
        super("Sfumature diverse - Riga",
                "Righe senza sfumature ripetute",
                5);
    }

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
