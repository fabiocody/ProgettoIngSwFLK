package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.util.Colors;
import java.util.*;
import java.util.stream.Collectors;


public class PublicObjectiveCard10 extends ObjectiveCard {

    public PublicObjectiveCard10() {
        super("Varietà di Colore",
                "Set di dadi di ogni colore ovunque",
                4);
    }

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
