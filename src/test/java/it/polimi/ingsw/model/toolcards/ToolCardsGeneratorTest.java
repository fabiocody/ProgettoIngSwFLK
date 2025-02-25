package it.polimi.ingsw.model.toolcards;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.shared.util.Constants;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;


class ToolCardsGeneratorTest {

    private static Game game;

    @BeforeEach
    void setup() {
        game = new Game(Stream.of("Fabio", "Luca", "Kai")
                .map(Player::new)
                .collect(Collectors.toList())
                , false);
        game.getDiceGenerator().generateDraftPool();
    }

    @Test
    void generatorTest() {
        List<ToolCard> cards = ToolCardsGenerator.generate(game);
        assertEquals(Constants.NUMBER_OF_TOOL_CARDS_PER_GAME, cards.size());
        assertNotEquals(cards.get(0), cards.get(1));
        assertNotEquals(cards.get(0), cards.get(2));
        assertNotEquals(cards.get(1), cards.get(2));
    }

}
