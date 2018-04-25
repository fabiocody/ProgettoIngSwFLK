package it.polimi.ingsw.toolcards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ToolCardsGenerator {

    public static List<ToolCard> generate() {
        List<ToolCard> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ToolCard newCard = null;
            do {
                String className = "it.polimi.ingsw.toolcards.ToolCard" + ThreadLocalRandom.current().nextInt(1, 13);
                try {
                    newCard = (ToolCard) Class.forName(className).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    // TODO
                    break;
                }
            } while (cards.contains(newCard));
            cards.add(newCard);
        }
        return cards;
    }

}
