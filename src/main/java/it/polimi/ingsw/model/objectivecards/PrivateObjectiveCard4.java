package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.shared.util.Colors;
import java.util.Arrays;


/**
 * @author Fabio Codiglioni
 */
public class PrivateObjectiveCard4 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    PrivateObjectiveCard4() {
        super("Sfumature Blu",
                "Somma dei valori su tutti i dadi blu");
    }

    /**
     * This method computes the Victory Points gained from the card
     *
     * @author Fabio Codiglioni
     * @param grid the grid of the player you want to compute Victory Points for.
     * @return the Victory Points gained from the card.
     */
    public int calcScore(Cell[] grid) {
        return Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(Cell::getPlacedDie)
                .filter(d -> d.getColor() == Colors.BLUE)
                .mapToInt(Die::getValue)
                .sum();
    }

}
