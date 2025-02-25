package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.shared.util.Constants;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * This class is used to generate both Public and Private Objective Cards
 *
 * @author Fabio Codiglioni
 */
public class ObjectiveCardsGenerator {

    private List<ObjectiveCard> generatedPrivates;
    private int numberOfPlayers;
    private boolean publicCardsAlreadyGenerated;

    /**
     * @author Fabio Codiglioni
     * @param numberOfPlayers the number of players participating
     */
    public ObjectiveCardsGenerator(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.publicCardsAlreadyGenerated = false;
    }

    /**
     * This method generates three distinct Public Objective Cards using Java Reflection
     *
     * @author Fabio Codiglioni
     * @return a list of Public Objective Cards only
     * @throws NoSuchObjectiveCardException this indicates that a card could not be found
     */
    public synchronized List<ObjectiveCard> generatePublicCards() {
        if (publicCardsAlreadyGenerated) throw new NoMoreCardsException();
        List<ObjectiveCard> generatedPublics = new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_PUB_OBJ_CARDS_PER_GAME; i++) {
            ObjectiveCard newCard;
            do {
                String className = "it.polimi.ingsw.model.objectivecards.PublicObjectiveCard" + ThreadLocalRandom.current().nextInt(1, Constants.NUMBER_OF_PUB_OBJ_CARDS + 1);
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

    /**
     * This method generates a number of distinct Private Objective Cards equal to the number of player
     *
     * @author Fabio Codiglioni
     */
    private synchronized void generatePrivateCards() {
        if (generatedPrivates == null) {
            generatedPrivates = new Vector<>();
            for (int i = 0; i < this.numberOfPlayers; i++) {
                ObjectiveCard newCard;
                do {
                    String className = "it.polimi.ingsw.model.objectivecards.PrivateObjectiveCard" + ThreadLocalRandom.current().nextInt(1, Constants.NUMBER_OF_PRI_OBJ_CARDS + 1);
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

    /**
     * This method must be called from each player to retrieve his own Private Objective Card
     *
     * @author Fabio Codiglioni
     * @return a Private Objective Card from the generated ones.
     * @see #generatePrivateCards()
     * @exception NoMoreCardsException this indicates that all the cards have been dealt.
     */
    public synchronized ObjectiveCard dealPrivate() {
        this.generatePrivateCards();
        if (generatedPrivates.isEmpty())
            throw new NoMoreCardsException();
        else
            return generatedPrivates.remove(0);
    }

    /**
     * @author Fabio Codiglioni
     * @return a string describing the state of the generator
     */
    public String toString() {
        return super.toString() + "\nRemaining public objective cards: " +
                (publicCardsAlreadyGenerated ? 0 : Constants.NUMBER_OF_PUB_OBJ_CARDS_PER_GAME) + "\nRemaining private objective cards: " +
                (generatedPrivates == null ? 4 : generatedPrivates.size());
    }

}
