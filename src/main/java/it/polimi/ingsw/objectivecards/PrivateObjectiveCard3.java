package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.Arrays;


public class PrivateObjectiveCard3 extends ObjectiveCard {

    public PrivateObjectiveCard3() {
        super("Sfumature Verdi",
                "Somma dei valori su tutti i dadi verdi");
    }

    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.GREEN)
                .mapToInt(Die::getValue)
                .sum();
    }
}
