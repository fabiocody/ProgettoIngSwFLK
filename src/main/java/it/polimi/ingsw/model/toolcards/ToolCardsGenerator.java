package it.polimi.ingsw.model.toolcards;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.shared.util.Constants;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * This class is used to generate the Tool Cards for a Game.
 *
 * @author Fabio Codiglioni
 */
public class ToolCardsGenerator {

    /**
     * This class only contains a static method, so this constructor should never be called.
     *
     * @author Fabio Codiglioni
     */
    private ToolCardsGenerator() {
        throw new IllegalStateException();
    }

    /**
     * @author Fabio Codiglioni
     * @param game the game the card is part of.
     * @return a <code>List</code> of three distinct Tool Cards.
     * @throws NoSuchToolCardException this indicates that a card could not be found.
     */
    public static List<ToolCard> generate(Game game) {
        List<ToolCard> cards = new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_TOOL_CARDS_PER_GAME; i++) {
            ToolCard newCard;
            do {
                String className = "it.polimi.ingsw.model.toolcards.ToolCard" + ThreadLocalRandom.current().nextInt(1, Constants.NUMBER_OF_TOOL_CARDS + 1);
                //String className = "it.polimi.ingsw.model.toolcards.ToolCard" + ThreadLocalRandom.current().nextInt(7, 10);
                try {
                    newCard = (ToolCard) Class.forName(className).getConstructor(Game.class).newInstance(game);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new NoSuchToolCardException(className);
                }
            } while (cards.contains(newCard));
            cards.add(newCard);
        }
        return cards;
    }

}
