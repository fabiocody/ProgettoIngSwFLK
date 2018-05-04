package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


public class PublicObjectiveCard8 extends ObjectiveCard {

    public PublicObjectiveCard8() {
        super("Sfumature Diverse",
                "Set di dadi di ogni valore ovunque",
                5);
    }

    public int calcScore(Cell[] grid) {
        int score = 0;
        Map<Integer, Long> map = Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(c -> c.getPlacedDie().getValue())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        if (map.size() == 6) {
            Optional<Long> min = map.values().stream().min(Comparator.naturalOrder());
            if (min.isPresent()) score += min.get() * this.getVictoryPoints();
        }
        return score;
    }

}
