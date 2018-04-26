package it.polimi.ingsw.objectivecards;


public class PrivateObjectiveCard5 extends ObjectiveCard {

    public PrivateObjectiveCard5() {
        super("Sfumature Viola",
                "Somma dei valori su tutti i dadi viola");
    }

    public int calcScore() {
        // TODO
        /*
         *  return Arrays.stream(grid).filter(x -> x.color == Colors.PURPLE).mapToInt(x -> x.value).sum()
         */
        return this.getVictoryPoints();
    }

}
