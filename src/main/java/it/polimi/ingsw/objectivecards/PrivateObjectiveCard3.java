package it.polimi.ingsw.objectivecards;


public class PrivateObjectiveCard3 extends ObjectiveCard {

    public PrivateObjectiveCard3() {
        super("Sfumature Verdi",
                "Somma dei valori su tutti i dadi verdi",
                null);
    }

    public int calcScore() {
        // TODO
        /*
         *  return Arrays.stream(Arrays.stream(grid).filter(x -> x.color == Colors.GREEN).mapToInt(x -> x.value).sum()
         */
        return this.getScore();
    }
}
