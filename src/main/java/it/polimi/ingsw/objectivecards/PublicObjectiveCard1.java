package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard1 extends ObjectiveCard {

    public PublicObjectiveCard1() {
        super("Colori diversi - Riga",
                "Righe senza colori ripetuti",
                6);
    }

    public int calcScore() {
        // TODO
        /*
         *  score = 0
         *  for row in grid
         *      if Arrays.stream(row).map(x -> x.color).distinct().collect(Collectors.toList()).size() == row.length
         *          score += this.getVictoryPoint()
         *  return score
         */
        return this.getVictoryPoints();
    }

}
