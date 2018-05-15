package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.Arrays;


public class PrivateObjectiveCard4 extends ObjectiveCard {

    public PrivateObjectiveCard4() {
        super("Sfumature Blu",
                "Somma dei valori su tutti i dadi blu");
    }

    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.BLUE)
                .mapToInt(Die::getValue)
                .sum();
    }

}
