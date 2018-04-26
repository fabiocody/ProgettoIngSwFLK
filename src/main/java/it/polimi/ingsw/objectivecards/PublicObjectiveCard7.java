package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard7 extends ObjectiveCard {

    public PublicObjectiveCard7() {
        super("Sfumature Scure",
                "Set di 5 & 6 ovunque",
                2);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0;
         *  Map<Integer, Long> map = Arrays.stream(grid).flatMap(Arrays::stream).map(Die::getValue).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
         *  Optional<Long> min = map.entrySet().stream().filter(x -> x.getKey() >= 5).map(Map.Entry::getValue).min(Comparator.naturalOrder());
         *  if (min.isPresent())
         *      score += min.get() * this.getVictoryPoint();
         *  return score;
         */
        return this.getVictoryPoints();
    }

}
