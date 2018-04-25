package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard10 extends ObjectiveCard {

    public PublicObjectiveCard10() {
        super("Varietà di Colore",
                "Set di dadi di ogni colore ovunque",
                4);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  Map<Colors, Long> map = Arrays.stream(row).map(x -> x.color).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
         *  if map.size() == 5
         *      min = map.values().stream().min(Comparator.naturalOrder())
         *      if min.isPresent()
         *          score += this.score * min.get();
         *  return score
         */
        return this.getScore();
    }

}
