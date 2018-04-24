package it.polimi.ingsw.objective_cards;

import java.util.List;
import java.util.Vector;


public class ObjectiveCardsGenerator {

    private List<ObjectiveCard> generatedPrivates;

    public List<ObjectiveCard> generatePublic() {
        // TODO
        return null;
    }

    private void generatePrivates() {
        if (generatedPrivates == null) {
            generatedPrivates = new Vector<ObjectiveCard>();
        }
        // TODO
    }

    public ObjectiveCard dealPrivate() {
        this.generatePrivates();
        // TODO
        return null;
    }

}
