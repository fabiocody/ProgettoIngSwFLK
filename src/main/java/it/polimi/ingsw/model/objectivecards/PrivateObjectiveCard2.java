package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.Arrays;


public class PrivateObjectiveCard2 extends ObjectiveCard {

    public PrivateObjectiveCard2() {
        super("Sfumature Gialle",
                "Somma dei valori su tutti i dadi gialli");
    }

    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.YELLOW)
                .mapToInt(Die::getValue)
                .sum();
    }

}
