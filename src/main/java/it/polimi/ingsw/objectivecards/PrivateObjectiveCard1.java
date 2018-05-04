package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.Arrays;


public class PrivateObjectiveCard1 extends ObjectiveCard {

    public PrivateObjectiveCard1() {
        super("Sfumature Rosse",
                "Somma dei valori su tutti i dati rossi");
    }

    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.RED)
                .mapToInt(Die::getValue)
                .sum();
    }

}
