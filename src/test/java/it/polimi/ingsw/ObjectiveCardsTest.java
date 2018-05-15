package it.polimi.ingsw;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.placementconstraints.EmptyConstraint;
import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class ObjectiveCardsTest {

    private static ObjectiveCardsGenerator generator;
    private static int numberOfPlayers = 3;

    @BeforeAll
    static void setUp() {
        generator = new ObjectiveCardsGenerator(numberOfPlayers);
    }

    @Test
    void publicGeneratorTest() {
        List<ObjectiveCard> cards = generator.generatePublic();
        assertEquals(3, cards.size());
        for (int i = 0; i < 3; i++)
            assertNotNull(cards.get(i));
        Set<ObjectiveCard> set = new HashSet<>(cards);
        assertEquals(cards.size(), set.size());
        assertThrows(NoMoreCardsException.class, () -> generator.generatePublic());
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

    @Test
    void privateObjectiveCard1() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 3), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 6), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 19, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 13, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PrivateObjectiveCard1();
        assertEquals(1+2+3+4+5, card.calcScore(window.getGrid()));
    }

    @Test
    void privateObjectiveCard2() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.YELLOW, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 3), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 19, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 13, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PrivateObjectiveCard2();
        assertEquals(1+2+3+4+5, card.calcScore(window.getGrid()));
    }

    @Test
    void privateObjectiveCard3() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.GREEN, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 19, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 13, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PrivateObjectiveCard3();
        assertEquals(1+2+3+4+5, card.calcScore(window.getGrid()));
    }

    @Test
    void privateObjectiveCard4() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.BLUE, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 3), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 1), 19, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 13, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PrivateObjectiveCard4();
        assertEquals(1+2+3+4+5, card.calcScore(window.getGrid()));
    }

    @Test
    void privateObjectiveCard5() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.PURPLE, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 19, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 13, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PrivateObjectiveCard5();
        assertEquals(1+2+3+4+5, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard1() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 2, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 5), 4, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 7, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard1();
        assertEquals(6, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard2() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard2();
        assertEquals(5, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard3() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 2, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 3, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 5), 4, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 1), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 7, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 3), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard3();
        assertEquals(5, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard4() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 1), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard4();
        assertEquals(6, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard5() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 1), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 1), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard5();
        assertEquals(6, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard6() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard6();
        assertEquals(2, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard7() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 5), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 1), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 6), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 1), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 6), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 5), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard7();
        assertEquals(4, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard8() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 3), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 5), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard8();
        assertEquals(5, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard9() {
        // TODO
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 2), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 3), 12, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 4), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 7, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard9();
        assertEquals(4+3, card.calcScore(window.getGrid()));
    }

    @Test
    void publicObjectiveCard10() {
        WindowPattern window = new WindowPattern(42);
        window.placeDie(new Die(Colors.RED, 1), 0, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 5, new EmptyConstraint());
        window.placeDie(new Die(Colors.GREEN, 1), 10, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 2), 15, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 1), 1, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 6), 6, new EmptyConstraint());
        window.placeDie(new Die(Colors.RED, 1), 11, new EmptyConstraint());
        window.placeDie(new Die(Colors.YELLOW, 2), 16, new EmptyConstraint());
        window.placeDie(new Die(Colors.PURPLE, 3), 8, new EmptyConstraint());
        window.placeDie(new Die(Colors.BLUE, 4), 9, new EmptyConstraint());
        ObjectiveCard card = new PublicObjectiveCard10();
        assertEquals(4, card.calcScore(window.getGrid()));
    }

}
