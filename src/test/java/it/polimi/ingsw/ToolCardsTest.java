package it.polimi.ingsw;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.placementconstraints.EmptyConstraint;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.toolcards.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class ToolCardsTest {

    private static Game game;

    @BeforeEach
    void setup() {
        game = new Game(Stream.of("Fabio", "Luca", "Kai")
                .map(Player::new)
                .collect(Collectors.toList())
        );
        game.getDiceGenerator().generateDraftPool();
    }

    @Test
    void generatorTest() {
        List<ToolCard> cards = ToolCardsGenerator.generate(game);
        assertEquals(3, cards.size());
        assertNotEquals(cards.get(0), cards.get(1));
        assertNotEquals(cards.get(0), cards.get(2));
        assertNotEquals(cards.get(1), cards.get(2));
    }

    @Test
    void toolCard1() {
        ToolCard toolCard = new ToolCard1(game);
        int oldValue = game.getDiceGenerator().getDraftPool().get(0).getValue();
        JsonObject data = new JsonObject();
        data.addProperty("player", "Fabio");
        data.addProperty("dieIndex", 0);
        if (oldValue <= 3)
            data.addProperty("delta", 1);
        else
            data.addProperty("delta", -1);
        try {
            toolCard.effect(data);
            if (oldValue <= 3) assertEquals(oldValue + 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
            else assertEquals(oldValue - 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
        data.remove("delta");
        data.addProperty("delta", 42);
        assertThrows(InvalidEffectResultException.class, () -> toolCard.effect(data));
    }

    @Test
    void toolCard2() {
        ToolCard toolCard = new ToolCard2(game);
        Player player = game.getPlayerForNickname("Fabio");
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        //player.getWindowPattern().placeDie(die, 0, PlacementConstraint.initialConstraint());
        // TODO Finire
    }

}
