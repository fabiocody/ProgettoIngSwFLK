package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;
import java.util.*;
import java.util.stream.Collectors;


public class PublicObjectiveCard6 extends ObjectiveCard {

    public PublicObjectiveCard6() {
        super("Sfumature Medie",
                "Set di 3 & 4 ovunque",
                2);
    }

    public int calcScore(Cell[] grid) {
        int score = 0;
        Map<Integer, Long> map = Arrays.stream(grid)
                .filter(c -> c.getPlacedDie() != null)
                .map(c -> c.getPlacedDie().getValue())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Optional<Long> min = map.entrySet().stream()
                .filter(e -> e.getKey() == 3 || e.getKey() == 4)
                .map(Map.Entry::getValue)
                .min(Comparator.naturalOrder());
        if (min.isPresent()) score += min.get() * this.getVictoryPoints();
        return score;
    }

}
