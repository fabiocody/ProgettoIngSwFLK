package it.polimi.ingsw;

import it.polimi.ingsw.toolcards.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class ToolCardsTest {

    @Test
    void generatorTest() {
        List<ToolCard> cards = ToolCardsGenerator.generate();
        assertEquals(3, cards.size());
        assertNotEquals(cards.get(0), cards.get(1));
        assertNotEquals(cards.get(0), cards.get(2));
        assertNotEquals(cards.get(1), cards.get(2));
    }

}
