package it.polimi.ingsw.objectivecards;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class ObjectiveCardsGenerator {

    private List<ObjectiveCard> generatedPrivates;
    private int numberOfPlayers;
    private boolean publicCardsAlreadyGenerated;

    public ObjectiveCardsGenerator(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.publicCardsAlreadyGenerated = false;
    }

    public synchronized List<ObjectiveCard> generatePublic() {
        if (publicCardsAlreadyGenerated) throw new NoMoreCardsException();
        List<ObjectiveCard> generatedPublics = new Vector<>();
        for (int i = 0; i < 3; i++) {
            ObjectiveCard newCard;
            do {
                String className = "it.polimi.ingsw.objectivecards.PublicObjectiveCard" + ThreadLocalRandom.current().nextInt(1, 11);
                try {
                    newCard = (ObjectiveCard)Class.forName(className).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new NoSuchObjectiveCardException(className);
                }
            } while (generatedPublics.contains(newCard));
            generatedPublics.add(newCard);
        }
        this.publicCardsAlreadyGenerated = true;
        return generatedPublics;
    }

    private synchronized void generatePrivates() {
        if (generatedPrivates == null) {
            generatedPrivates = new Vector<>();
            for (int i = 0; i < this.numberOfPlayers; i++) {
                ObjectiveCard newCard;
                do {
                    String className = "it.polimi.ingsw.objectivecards.PrivateObjectiveCard" + ThreadLocalRandom.current().nextInt(1, 6);
                    try {
                        newCard = (ObjectiveCard) Class.forName(className).newInstance();
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        throw new NoSuchObjectiveCardException(className);
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

    public String toString() {
        return super.toString() + "\nRemaining public objective cards: " + (publicCardsAlreadyGenerated ? 0 : 3) + "\nRemaining private objective cards: " + (generatedPrivates == null ? 4 : generatedPrivates.size());
    }

}
