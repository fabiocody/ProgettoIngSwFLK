package it.polimi.ingsw;

import it.polimi.ingsw.objectivecards.ObjectiveCard;
import it.polimi.ingsw.objectivecards.ObjectiveCardsGenerator;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class ObjectiveCardsTest {

    private static ObjectiveCardsGenerator generator;

    @BeforeAll
    static void setUp() {
        generator = new ObjectiveCardsGenerator(4);
    }

    @Test
    void publicCardsTest() {
        List<ObjectiveCard> cards = generator.generatePublic();
        assertEquals(3, cards.size());
        assertNotEquals(cards.get(0), cards.get(1));
        assertNotEquals(cards.get(0), cards.get(2));
        assertNotEquals(cards.get(1), cards.get(2));
    }

    @Test
    void privateCardsTest() {

    }

}
