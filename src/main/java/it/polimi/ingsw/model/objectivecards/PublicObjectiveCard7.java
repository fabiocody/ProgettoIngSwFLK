package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Fabio Codiglioni
 */
public class PublicObjectiveCard7 extends ObjectiveCard {

    /**
     * This constructor initializes the card with its name and description
     *
     * @author Fabio Codiglioni
     */
    public PublicObjectiveCard7() {
        super("Sfumature Scure",
                "Set di 5 & 6 ovunque",
                2);
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
        Map<Integer, Long> map = Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(c -> c.getPlacedDie().getValue())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Optional<Long> min = map.entrySet().stream()
                .filter(e -> e.getKey() >= 5)
                .map(Map.Entry::getValue)
                .min(Comparator.naturalOrder());
        if (min.isPresent()) score += min.get() * this.getVictoryPoints();
        return score;
    }

}
