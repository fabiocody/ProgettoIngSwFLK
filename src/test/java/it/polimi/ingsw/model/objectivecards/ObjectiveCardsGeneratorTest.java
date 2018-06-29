package it.polimi.ingsw.model.objectivecards;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class ObjectiveCardsGeneratorTest {

    private ObjectiveCardsGenerator generator;
    private int numberOfPlayers = 3;

    @BeforeEach
    void setUp() {
        generator = new ObjectiveCardsGenerator(numberOfPlayers);
    }

    @Test
    void publicGeneratorTest() {
        List<ObjectiveCard> cards = generator.generatePublicCards();
        assertEquals(3, cards.size());
        for (int i = 0; i < 3; i++)
            assertNotNull(cards.get(i));
        Set<ObjectiveCard> set = new HashSet<>(cards);
        assertEquals(cards.size(), set.size());
        assertThrows(NoMoreCardsException.class, () -> generator.generatePublicCards());
    }

    @Test
    void privateGeneratorTest() {
        List<ObjectiveCard> cards = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            cards.add(generator.dealPrivate());
            assertNotNull(cards.get(i));
        }
        assertEquals(numberOfPlayers, cards.size());
        Set<ObjectiveCard> set = new HashSet<>(cards);
        assertEquals(cards.size(), set.size());
        assertThrows(NoMoreCardsException.class, () -> cards.add(generator.dealPrivate()));
        assertEquals(numberOfPlayers, cards.size());
        set = new HashSet<>(cards);
        assertEquals(cards.size(), set.size());
    }

}
