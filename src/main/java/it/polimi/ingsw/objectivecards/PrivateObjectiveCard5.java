package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.Arrays;


public class PrivateObjectiveCard5 extends ObjectiveCard {

    public PrivateObjectiveCard5() {
        super("Sfumature Viola",
                "Somma dei valori su tutti i dadi viola");
    }

    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.PURPLE)
                .mapToInt(Die::getValue)
                .sum();
    }

}
