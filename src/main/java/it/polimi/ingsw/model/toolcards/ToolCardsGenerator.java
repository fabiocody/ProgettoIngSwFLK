package it.polimi.ingsw.model.toolcards;

import it.polimi.ingsw.server.Game;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class ToolCardsGenerator {

    private ToolCardsGenerator() {
        throw new IllegalStateException();
    }

    public static List<ToolCard> generate(Game game) {
        List<ToolCard> cards = new Vector<>();
        for (int i = 0; i < 3; i++) {
            ToolCard newCard;
            do {
                String className = "it.polimi.ingsw.model.toolcards.ToolCard" + ThreadLocalRandom.current().nextInt(1, 13);
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
