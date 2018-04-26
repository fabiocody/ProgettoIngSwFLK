package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard5 extends ObjectiveCard {

    public PublicObjectiveCard5() {
        super("Sfumature Chiare",
                "Set di 1 & 2 ovunque",
                2);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0;
         *  Map<Integer, Long> map = Arrays.stream(grid).flatMap(Arrays::stream).map(Die::getValue).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
         *  Optional<Long> min = map.entrySet().stream().filter(x -> x.getKey() <= 2).map(Map.Entry::getValue).min(Comparator.naturalOrder());
         *  if (min.isPresent())
         *      score += min.get() * this.getVictoryPoint;
         *  return score;
         */
        return this.getVictoryPoints();
    }

}
