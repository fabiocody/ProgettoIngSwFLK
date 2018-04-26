package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard2 extends ObjectiveCard {

    public PublicObjectiveCard2() {
        super("Colori diversi - Colonna",
                "Colonne senza colori ripetuti",
                5);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  for column in grid
         *      if Arrays.stream(column).map(x -> x.color).distinct().collect(Collectors.toList()).size() == column.length
         *          score += this.getVictoryPoints()
         *  return score
         */
        return this.getVictoryPoints();
    }

}
