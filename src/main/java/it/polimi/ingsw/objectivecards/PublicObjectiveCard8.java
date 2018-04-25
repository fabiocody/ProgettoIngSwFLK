package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard8 extends ObjectiveCard {

    public PublicObjectiveCard8() {
        super("Sfumature Diverse",
                "Set di dadi di ogni valore ovunque",
                5);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  Map<Integer, Long> map = Arrays.stream(row).map(x -> x.value).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
         *  if map.size() == 5
         *      min = map.values().stream().min(Comparator.naturalOrder())
         *      if min.isPresent()
         *          score += this.score * min.get();
         *  return score
         */
        return this.getScore();
    }

}
