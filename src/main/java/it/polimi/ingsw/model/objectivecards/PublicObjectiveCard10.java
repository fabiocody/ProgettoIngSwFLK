package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard10 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    public PublicObjectiveCard10() {
        super("Varietà di Colore",
                "Set di dadi di ogni colore ovunque",
                4);
    }

    /**
     * This method computes the Victory Points gained from the card
     *
     * @author Fabio Codiglioni
     * @param grid the grid of the player you want to compute Victory Points for.
     * @return the Victory Points gained from the card.
     */
    public int calcScore(Cell[] grid) {
        int score = 0;
        Map<Colors, Long> map = Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(c -> c.getPlacedDie().getColor())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        if (map.size() == 5) {
            Optional<Long> min = map.values().stream().min(Comparator.naturalOrder());
            if (min.isPresent()) score += min.get() * this.getVictoryPoints();
        }
        return score;
    }

}
