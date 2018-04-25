package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard4 extends ObjectiveCard {

    public PublicObjectiveCard4() {
        super("Sfumature diverse - Colonna",
                "Colonne senza sfumature ripetute",
                6);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  for column in grid
         *      if Arrays.stream(column).map(x -> x.value).distinct().collect(Collectors.toList()).size() == column.length
         *          score += this.score
         *  return score
         */
        return this.getScore();
    }

}
