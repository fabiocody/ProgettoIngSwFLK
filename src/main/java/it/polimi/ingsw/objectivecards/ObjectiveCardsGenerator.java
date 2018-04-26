package it.polimi.ingsw.objectivecards;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;


public class ObjectiveCardsGenerator {

    private List<ObjectiveCard> generatedPrivates;
    private int numberOfPlayers;

    public ObjectiveCardsGenerator(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public List<ObjectiveCard> generatePublic() {
        List<ObjectiveCard> generatedPublics = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ObjectiveCard newCard = null;
            do {
                String className = "it.polimi.ingsw.objectivecards.PublicObjectiveCard" + ThreadLocalRandom.current().nextInt(1, 11);
                try {
                    newCard = (ObjectiveCard)Class.forName(className).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    // TODO
                    break;
                }
            } while (generatedPublics.contains(newCard));
            generatedPublics.add(newCard);
        }
        return generatedPublics;
    }

    private void generatePrivates() {
        if (generatedPrivates == null) {
            generatedPrivates = new Vector<>();
            for (int i = 0; i < this.numberOfPlayers; i++) {
                ObjectiveCard newCard = null;
                do {
                    String className = "it.polimi.ingsw.objectivecards.PrivateObjectiveCard" + ThreadLocalRandom.current().nextInt(1, 6);
                    try {
                        newCard = (ObjectiveCard) Class.forName(className).newInstance();
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        // TODO
                        break;
                    }
                } while (generatedPrivates.contains(newCard));
                generatedPrivates.add(newCard);
            }
        }
    }

    public synchronized ObjectiveCard dealPrivate() {
        this.generatePrivates();
        if (generatedPrivates.isEmpty())
            throw new NoMoreCardsException();
        else
            return generatedPrivates.remove(0);
    }

}
