package it.polimi.ingsw;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.WindowPattern;
import it.polimi.ingsw.placementconstraints.EmptyConstraint;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.toolcards.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class ToolCardsTest {

    private static Game game;
    private static Player player;

    @BeforeEach
    void setup() {
        game = new Game(Stream.of("Fabio", "Luca", "Kai")
                .map(Player::new)
                .collect(Collectors.toList())
        , false);
        game.getDiceGenerator().generateDraftPool();
        player = game.getPlayerForNickname("Fabio");
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
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCellAt(2).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 8);
        assertNotNull(player.getWindowPattern().getCellAt(8).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 2);
        data.addProperty("fromCellY", 0);
        data.addProperty("toCellX", 4);
        data.addProperty("toCellY", 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(2, 4).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard3() {
        ToolCard toolCard = new ToolCard3(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCellAt(17).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 11);
        assertNotNull(player.getWindowPattern().getCellAt(11).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 2);
        data.addProperty("fromCellY", 3);
        data.addProperty("toCellX", 2);
        data.addProperty("toCellY", 1);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(3, 2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(1, 2).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard4() {
        ToolCard toolCard = new ToolCard4(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(2)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCellAt(17).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 11);
        assertNotNull(player.getWindowPattern().getCellAt(11).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 2);
        data.addProperty("fromCellY", 3);
        data.addProperty("toCellX", 2);
        data.addProperty("toCellY", 1);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(3, 2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(1, 2).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 1);
        data.addProperty("fromCellY", 2);
        data.addProperty("toCellX", 3);
        data.addProperty("toCellY", 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(2, 1).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(2, 3).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard5() {
        ToolCard toolCard = new ToolCard5(game);
        game.getRoundTrack().putDie(game.getDiceGenerator().getDraftPool());
        game.getDiceGenerator().generateDraftPool();
        int draftPoolIndex = ThreadLocalRandom.current().nextInt(0, game.getDiceGenerator().getDraftPool().size());
        int roundTrackIndex = ThreadLocalRandom.current().nextInt(0, game.getRoundTrack().getDice().size());
        Die fromDraftPool = game.getDiceGenerator().getDraftPool().get(draftPoolIndex);
        Die fromRoundTrack = game.getRoundTrack().getDice().get(roundTrackIndex);
        JsonObject data = new JsonObject();
        data.addProperty("draftPoolIndex", draftPoolIndex);
        data.addProperty("roundTrackIndex", roundTrackIndex);
        try {
            toolCard.effect(data);
            assertEquals(fromDraftPool, game.getRoundTrack().getDice().get(roundTrackIndex));
            assertEquals(fromRoundTrack, game.getDiceGenerator().getDraftPool().get(draftPoolIndex));
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard6DontPutAway() {
        ToolCard toolCard = new ToolCard6(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(2)));
        Die die = game.getDiceGenerator().getDraftPool().get(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        die = game.getDiceGenerator().getDraftPool().get(0);
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("draftPoolIndex", 0);
        try {
            toolCard.effect(data);
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
        data.addProperty("x", 1);
        data.addProperty("y", 2);
        data.addProperty("putAway", false);
        try {
            toolCard.effect(data);
            assertEquals(die, player.getWindowPattern().getCellAt(2, 1).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard6PutAway() {
        ToolCard toolCard = new ToolCard6(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(2)));
        Die die = game.getDiceGenerator().getDraftPool().get(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        die = game.getDiceGenerator().getDraftPool().get(0);
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("draftPoolIndex", 0);
        try {
            toolCard.effect(data);
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
        data.addProperty("putAway", true);
        try {
            toolCard.effect(data);
            assertEquals(die, game.getDiceGenerator().getDraftPool().get(0));
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard7() {
        // Check that at least one die has changed value
        ToolCard toolCard = new ToolCard7(game);
        while (!game.getTurnManager().isSecondHalfOfRound())
            game.getTurnManager().nextTurn();
        List<Integer> oldDraftPoolValues = game.getDiceGenerator().getDraftPool().stream()
                .map(Die::getValue)
                .collect(Collectors.toList());
        try {
            toolCard.effect(null);
            List<Die> list = new ArrayList<>();
            for (int i = 0; i < game.getDiceGenerator().getDraftPool().size(); i++) {
                if (game.getDiceGenerator().getDraftPool().get(i).getValue() != oldDraftPoolValues.get(i))
                    list.add(game.getDiceGenerator().getDraftPool().get(i));
            }
            assertTrue(list.size() > 0);
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard8() {
        // TODO vedi ToolCard8
        ToolCard toolCard = new ToolCard8(game);
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        try {
            toolCard.effect(data);
            assertTrue(player.isSecondTurnToBeJumped());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard9() {
        // TODO
        ToolCard toolCard = new ToolCard9(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCellAt(17).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("draftPoolIndex", 0);
        data.addProperty("cellX", 2);
        data.addProperty("cellY", 0);
        try {
            toolCard.effect(data);
            assertNotNull(player.getWindowPattern().getCellAt(0, 2).getPlacedDie());
        } catch (InvalidEffectResultException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard10() {
        // TODO
    }

    @Test
    void toolCard11() {
        // TODO
    }

    @Test
    void toolCard12() {
        // TODO
    }

}
