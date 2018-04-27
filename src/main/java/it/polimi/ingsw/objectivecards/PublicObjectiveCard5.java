package it.polimi.ingsw.objectivecards;

import it.polimi.ingsw.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


public class PublicObjectiveCard5 extends ObjectiveCard {

    public PublicObjectiveCard5() {
        super("Sfumature Chiare",
                "Set di 1 & 2 ovunque",
                2);
    }

    public int calcScore(Cell[] grid) {
        int score = 0;
        Map<Integer, Long> map = Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(c -> c.getPlacedDie().getValue())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Optional<Long> min = map.entrySet().stream()
                .filter(e -> e.getKey() <= 2)
                .map(Map.Entry::getValue)
                .min(Comparator.naturalOrder());
        if (min.isPresent()) score += min.get() * this.getVictoryPoints();
        return score;
    }

}
