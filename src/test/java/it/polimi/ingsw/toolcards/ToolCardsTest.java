package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.Colors;
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
    void toolCard1() {
        ToolCard toolCard = new ToolCard1(game);
        int oldValue = game.getDiceGenerator().getDraftPool().get(0).getValue();
        JsonObject data = new JsonObject();
        data.addProperty("player", "Fabio");
        data.addProperty("draftPoolIndex", 0);
        if (oldValue <= 3)
            data.addProperty("delta", 1);
        else
            data.addProperty("delta", -1);
        try {
            toolCard.effect(data);
            if (oldValue <= 3) assertEquals(oldValue + 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
            else assertEquals(oldValue - 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.remove("delta");
        data.addProperty("delta", 42);
        assertThrows(InvalidEffectArgumentException.class, () -> toolCard.effect(data));
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard5() {
        ToolCard toolCard = new ToolCard5(game);
        game.getRoundTrack().putDice(game.getDiceGenerator().getDraftPool());
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.addProperty("cellX", 1);
        data.addProperty("cellY", 2);
        data.addProperty("putAway", false);
        try {
            toolCard.effect(data);
            assertEquals(die, player.getWindowPattern().getCellAt(2, 1).getPlacedDie());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.addProperty("putAway", true);
        try {
            toolCard.effect(data);
            assertEquals(die, game.getDiceGenerator().getDraftPool().get(0));
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
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
            assertTrue(player.isSecondTurnToBeSkipped());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard9() {
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
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard10() {
        ToolCard toolCard = new ToolCard10(game);
        int oldValue = game.getDiceGenerator().getDraftPool().get(0).getValue();
        JsonObject data = new JsonObject();
        data.addProperty("draftPoolIndex", 0);
        try {
            toolCard.effect(data);
            assertEquals(7 - oldValue, game.getDiceGenerator().getDraftPool().get(0).getValue());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard11() {
        ToolCard toolCard = new ToolCard11(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("draftPoolIndex", 0);
        Die oldDie = game.getDiceGenerator().getDraftPool().get(0);
        try {
            toolCard.effect(data);
            assertNotEquals(oldDie, game.getDiceGenerator().getDraftPool().get(0));
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.addProperty("newValue", 4);
        try {
            toolCard.effect(data);
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        die = game.getDiceGenerator().getDraftPool().get(0);
        data.addProperty("cellX", 1);
        data.addProperty("cellY", 2);
        try {
            toolCard.effect(data);
            assertEquals(die, player.getWindowPattern().getCellAt(2, 1).getPlacedDie());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard12DontStop() {
        ToolCard toolCard = new ToolCard12(game);
        game.getRoundTrack().getDice().add(new Die(Colors.RED, 6));
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = new Die(Colors.RED, 2);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        die = new Die(Colors.RED, 4);
        player.getWindowPattern().placeDie(die, 8);
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 2);
        data.addProperty("fromCellY", 0);
        data.addProperty("toCellX", 2);
        data.addProperty("toCellY", 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(0, 2 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(2, 2).getPlacedDie());
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 3);
        data.addProperty("fromCellY", 1);
        data.addProperty("toCellX", 1);
        data.addProperty("toCellY", 3);
        data.addProperty("stop", false);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(1, 3 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(3, 1).getPlacedDie());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard12Stop() {
        ToolCard toolCard = new ToolCard12(game);
        game.getRoundTrack().getDice().add(new Die(Colors.RED, 6));
        player.setWindowPatternList(Arrays.asList(new WindowPattern(0)));
        Die die = new Die(Colors.RED, 2);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        die = new Die(Colors.RED, 4);
        player.getWindowPattern().placeDie(die, 8);
        JsonObject data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("fromCellX", 2);
        data.addProperty("fromCellY", 0);
        data.addProperty("toCellX", 2);
        data.addProperty("toCellY", 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCellAt(0, 2 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCellAt(2, 2).getPlacedDie());
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty("player", player.getNickname());
        data.addProperty("stop", true);
        try {
            toolCard.effect(data);
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

}
