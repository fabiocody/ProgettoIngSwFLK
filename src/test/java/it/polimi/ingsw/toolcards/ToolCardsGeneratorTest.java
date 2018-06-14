package it.polimi.ingsw.toolcards;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.Constants;
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
        assertEquals(Constants.TOOL_CARD_NUMBER, cards.size());
        assertNotEquals(cards.get(0), cards.get(1));
        assertNotEquals(cards.get(0), cards.get(2));
        assertNotEquals(cards.get(1), cards.get(2));
    }

}
