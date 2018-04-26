package it.polimi.ingsw.objectivecards;


public class PrivateObjectiveCard4 extends ObjectiveCard {

    public PrivateObjectiveCard4() {
        super("Sfumature Blu",
                "Somma dei valori su tutti i dadi blu");
    }

    public int calcScore() {
        // TODO
        /*
         *  return Arrays.stream(grid).filter(x -> x.color == Colors.BLUE).mapToInt(x -> x.value).sum()
         */
        return this.getVictoryPoints();
    }

}
