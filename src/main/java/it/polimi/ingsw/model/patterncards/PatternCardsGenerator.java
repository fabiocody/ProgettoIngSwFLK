package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.shared.util.Constants;
import java.util.*;


/**
 * This class manages the creation and distribution of cards for each player.
 * @author  Luca dell'Oglio
 */
public class PatternCardsGenerator {

    private final List<WindowPattern> generatedCards = new ArrayList<>();

    /**
     * This method creates 2 cards for each player. Each card contains randomly selected odd number pattern and its
     * consecutive.
     *
     * @param   numberOfPlayers the number of players playing the game
     * @throws  InvalidNumberOfPlayersException thrown when numberOfPlayers does not comply to the game rule
     * @author  Luca dell'Oglio
     */
    public PatternCardsGenerator(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > Constants.MAX_NUMBER_OF_PLAYERS)
            throw new InvalidNumberOfPlayersException();
        List <Integer> randomNumbers= new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_PATTERNS/2; i++)
            randomNumbers.add(i);
        Collections.shuffle(randomNumbers);
        for (int i = 0; i < numberOfPlayers*Constants.PATTERN_CARDS_FOR_EACH_PLAYER; i++) {
            generatedCards.add(new WindowPattern(2*(randomNumbers.get(i))));
            generatedCards.add(new WindowPattern((2*randomNumbers.get(i))+1));
        }
    }

    /**
     * @return  the cards generated
     * @author  Luca dell'Oglio
     */
    public synchronized List<WindowPattern> getCards(){
        return this.generatedCards;
    }

    /**
     * @return  the 2 cards (that will be also removed from <code>generatedCards</code> from which the player will
     *          choose his/her pattern.
     * @throws  IllegalStateException
     * @author  Luca dell'Oglio
     */
    public synchronized List<WindowPattern> getCardsForPlayer(){
        if (this.generatedCards.isEmpty())
            throw new IllegalStateException("No more pattern cards");
        List<WindowPattern> card = new ArrayList<>();
        for (int i = 0; i < 2*Constants.PATTERN_CARDS_FOR_EACH_PLAYER; i++) card.add(this.generatedCards.remove(0));
        return card;
    }
}