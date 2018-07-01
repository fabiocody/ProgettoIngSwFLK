package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.shared.util.Colors;
import it.polimi.ingsw.model.dice.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static it.polimi.ingsw.shared.util.JsonFields.*;
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
        player = game.getPlayer("Fabio");
    }

    @Test
    void toolCard1() {
        ToolCard toolCard = new ToolCard1(game);
        int oldValue = game.getDiceGenerator().getDraftPool().get(0).getValue();
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, "Fabio");
        data.addProperty(DRAFT_POOL_INDEX, 0);
        if (oldValue <= 3)
            data.addProperty(DELTA, 1);
        else
            data.addProperty(DELTA, -1);
        try {
            toolCard.effect(data);
            if (oldValue <= 3) assertEquals(oldValue + 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
            else assertEquals(oldValue - 1, game.getDiceGenerator().getDraftPool().get(0).getValue());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.remove(DELTA);
        data.addProperty(DELTA, Constants.INDEX_CONSTANT); //define
        assertThrows(InvalidEffectArgumentException.class, () -> toolCard.effect(data));
    }

    @Test
    void toolCard2() {
        ToolCard toolCard = new ToolCard2(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCell(2).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 8);
        assertNotNull(player.getWindowPattern().getCell(8).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 2);
        data.addProperty(FROM_CELL_Y, 0);
        data.addProperty(TO_CELL_X, 4);
        data.addProperty(TO_CELL_Y, 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(2, 4).getPlacedDie());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard3() {
        ToolCard toolCard = new ToolCard3(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCell(17).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 11);
        assertNotNull(player.getWindowPattern().getCell(11).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 2);
        data.addProperty(FROM_CELL_Y, 3);
        data.addProperty(TO_CELL_X, 2);
        data.addProperty(TO_CELL_Y, 1);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(3, 2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(1, 2).getPlacedDie());
            //assertTrue(toolCard.isUsed());
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
        assertNotNull(player.getWindowPattern().getCell(17).getPlacedDie());
        die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 11);
        assertNotNull(player.getWindowPattern().getCell(11).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 2);
        data.addProperty(FROM_CELL_Y, 3);
        data.addProperty(TO_CELL_X, 2);
        data.addProperty(TO_CELL_Y, 1);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(3, 2).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(1, 2).getPlacedDie());
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 1);
        data.addProperty(FROM_CELL_Y, 2);
        data.addProperty(TO_CELL_X, 3);
        data.addProperty(TO_CELL_Y, 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(2, 1).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(2, 3).getPlacedDie());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard5() {
        ToolCard toolCard = new ToolCard5(game);
        game.getRoundTrack().incrementRound();
        game.getRoundTrack().putDice(game.getDiceGenerator().getDraftPool());
        game.getDiceGenerator().generateDraftPool();
        int draftPoolIndex = ThreadLocalRandom.current().nextInt(0, game.getDiceGenerator().getDraftPool().size());
        int roundTrackIndex = ThreadLocalRandom.current().nextInt(0, game.getRoundTrack().getFlattenedDice().size());
        Die fromDraftPool = game.getDiceGenerator().getDraftPool().get(draftPoolIndex);
        Die fromRoundTrack = game.getRoundTrack().getDice().get(game.getRoundTrack().getCurrentRoundDiceIndex()).get(roundTrackIndex);
        JsonObject data = new JsonObject();
        data.addProperty(DRAFT_POOL_INDEX, draftPoolIndex);
        data.addProperty(ROUND_TRACK_INDEX, roundTrackIndex);
        try {
            toolCard.effect(data);
            assertEquals(fromDraftPool, game.getRoundTrack().getFlattenedDice().get(roundTrackIndex));
            assertEquals(fromRoundTrack, game.getDiceGenerator().getDraftPool().get(draftPoolIndex));
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    // This test is no longer possible
    /*@Test
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
            assertEquals(die, player.getWindowPattern().getCell(2, 1).getPlacedDie());
            assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }*/

    // This test is no longer possible
    /*@Test
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
    }*/

    // TODO Enable these tests once all the Tool Cards are fixed
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
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard8() {
        // TODO vedi ToolCard8
        ToolCard toolCard = new ToolCard8(game);
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        data.addProperty(DRAFT_POOL_INDEX, 0);
        data.addProperty(TO_CELL_X, 0);
        data.addProperty(TO_CELL_Y, 1);
        try {
            toolCard.effect(data);
            assertTrue(player.isSecondTurnToBeSkipped());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard9() {
        ToolCard toolCard = new ToolCard9(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        assertNotNull(player.getWindowPattern().getCell(17).getPlacedDie());
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(DRAFT_POOL_INDEX, 0);
        data.addProperty(TO_CELL_X, 2);
        data.addProperty(TO_CELL_Y, 0);
        try {
            toolCard.effect(data);
            assertNotNull(player.getWindowPattern().getCell(0, 2).getPlacedDie());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard10() {
        ToolCard toolCard = new ToolCard10(game);
        int oldValue = game.getDiceGenerator().getDraftPool().get(0).getValue();
        JsonObject data = new JsonObject();
        data.addProperty(DRAFT_POOL_INDEX, 0);
        try {
            toolCard.effect(data);
            assertEquals(7 - oldValue, game.getDiceGenerator().getDraftPool().get(0).getValue());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard11() {
        ToolCard toolCard = new ToolCard11(game);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        //Die die = game.getDiceGenerator().drawDieFromDraftPool(0);
        //player.getWindowPattern().placeDie(die, 17, PlacementConstraint.initialConstraint());
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(DRAFT_POOL_INDEX, 0);
        try {
            toolCard.effect(data);
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data.addProperty(NEW_VALUE, 4);
        /*try {
            toolCard.effect(data);
            assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }*/
        Die die = game.getDiceGenerator().getDraftPool().get(0);
        data.addProperty(TO_CELL_X, 3);
        data.addProperty(TO_CELL_Y, 0);
        try {
            toolCard.effect(data);
            assertEquals(die, player.getWindowPattern().getCell(0, 3).getPlacedDie());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard12DontStop() {
        ToolCard toolCard = new ToolCard12(game);
        List<Die> test = new ArrayList<>();
        test.add(new Die(Colors.RED,6));
        game.getRoundTrack().incrementRound();
        game.getRoundTrack().putDice(test);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        Die die = new Die(Colors.RED, 2);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        die = new Die(Colors.RED, 4);
        player.getWindowPattern().placeDie(die, 8);
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 2);
        data.addProperty(FROM_CELL_Y, 0);
        data.addProperty(TO_CELL_X, 2);
        data.addProperty(TO_CELL_Y, 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(0, 2 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(2, 2).getPlacedDie());
            //assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 3);
        data.addProperty(FROM_CELL_Y, 1);
        data.addProperty(TO_CELL_X, 1);
        data.addProperty(TO_CELL_Y, 3);
        data.addProperty(STOP, false);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(1, 3 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(3, 1).getPlacedDie());
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void toolCard12Stop() {
        ToolCard toolCard = new ToolCard12(game);
        List<Die> test = new ArrayList<>();
        test.add(new Die(Colors.RED,6));
        game.getRoundTrack().incrementRound();
        game.getRoundTrack().putDice(test);
        player.setWindowPatternList(Arrays.asList(new WindowPattern(Constants.INDEX_CONSTANT)));
        Die die = new Die(Colors.RED, 2);
        player.getWindowPattern().placeDie(die, 2, PlacementConstraint.initialConstraint());
        die = new Die(Colors.RED, 4);
        player.getWindowPattern().placeDie(die, 8);
        JsonObject data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(FROM_CELL_X, 2);
        data.addProperty(FROM_CELL_Y, 0);
        data.addProperty(TO_CELL_X, 2);
        data.addProperty(TO_CELL_Y, 2);
        try {
            toolCard.effect(data);
            assertNull(player.getWindowPattern().getCell(0, 2 ).getPlacedDie());
            assertNotNull(player.getWindowPattern().getCell(2, 2).getPlacedDie());
            //assertFalse(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
        data = new JsonObject();
        data.addProperty(PLAYER, player.getNickname());
        data.addProperty(STOP, true);
        try {
            toolCard.effect(data);
            //assertTrue(toolCard.isUsed());
        } catch (InvalidEffectResultException | InvalidEffectArgumentException e) {
            e.printStackTrace();
        }
    }

}
