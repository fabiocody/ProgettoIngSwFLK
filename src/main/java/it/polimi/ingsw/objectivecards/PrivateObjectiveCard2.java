package it.polimi.ingsw.objectivecards;


public class PrivateObjectiveCard2 extends ObjectiveCard {

    public PrivateObjectiveCard2() {
        super("Sfumature Gialle",
                "Somma dei valori su tutti i dadi gialli",
                null);
    }

    public int calcScore() {
        // TODO
        /*
         *  return Arrays.stream(Arrays.stream(grid).filter(x -> x.color == Colors.YELLOW).mapToInt(x -> x.value).sum()
         */
        return this.getScore();
    }

}
