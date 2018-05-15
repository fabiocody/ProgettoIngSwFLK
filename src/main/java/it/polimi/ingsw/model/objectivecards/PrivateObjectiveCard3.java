package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
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
