package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.patterncards.Cell;
import java.util.Arrays;
import java.util.stream.Collectors;


public class PublicObjectiveCard1 extends ObjectiveCard {

    public PublicObjectiveCard1() {
        super("Colori diversi - Riga",
                "Righe senza colori ripetuti",
                6);
    }

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
