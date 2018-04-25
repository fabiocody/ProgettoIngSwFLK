package it.polimi.ingsw.objectivecards;


public class PrivateObjectiveCard1 extends ObjectiveCard {

    public PrivateObjectiveCard1() {
        super("Sfumature Rosse",
                "Somma dei valori su tutti i dati rossi",
                null);
    }

    public int calcScore() {
        // TODO
        /*
         *  return Arrays.stream(Arrays.stream(grid).filter(x -> x.color == Colors.RED).mapToInt(x -> x.value).sum()
         */
        return this.getScore();
    }

}
