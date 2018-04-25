package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard3 extends ObjectiveCard {

    public PublicObjectiveCard3() {
        super("Sfumature diverse - Riga",
                "Righe senza sfumature ripetute",
                5);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  for row in grid
         *      if Arrays.stream(row).map(x -> x.value).distinct().collect(Collectors.toList()).size() == row.length
         *          score += this.score
         *  return score
         */
        return this.getScore();
    }

}
