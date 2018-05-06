package it.polimi.ingsw.toolcards;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class ToolCardsGenerator {

    private ToolCardsGenerator() {
        throw new IllegalStateException();
    }

    public static List<ToolCard> generate() {
        List<ToolCard> cards = new Vector<>();
        for (int i = 0; i < 3; i++) {
            ToolCard newCard;
            do {
                String className = "it.polimi.ingsw.toolcards.ToolCard" + ThreadLocalRandom.current().nextInt(1, 13);
                try {
                    newCard = (ToolCard) Class.forName(className).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new NoSuchToolCardException(className);
                }
            } while (cards.contains(newCard));
            cards.add(newCard);
        }
        return cards;
    }

}
