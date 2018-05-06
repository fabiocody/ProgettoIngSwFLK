package it.polimi.ingsw;

import it.polimi.ingsw.objectivecards.ObjectiveCardsGenerator;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.server.*;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;


class ServerTest {

    @Test
    void playerTest() {
        Player player = new Player("James");
        WindowPattern window = new PatternCardsGenerator(2).getCard().get(0);
        ObjectiveCardsGenerator objectiveCardsGenerator = new ObjectiveCardsGenerator(2);
        assertThrows(IllegalStateException.class, player::getWindowPattern);
        player.setWindowPattern(window);
        assertNotNull(player.getWindowPattern());
        assertEquals(window.getDifficulty(), player.getFavorTokens());
        assertThrows(IllegalStateException.class, () -> player.setWindowPattern(window));
        assertThrows(IllegalStateException.class, player::getPrivateObjectiveCard);
        player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate());
        assertNotNull(player.getPrivateObjectiveCard());
        assertThrows(IllegalStateException.class, () -> player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate()));
    }

    @Test
    void roundTrackTest() {
        RoundTrack roundTrack = new RoundTrack();
        assertEquals(1, roundTrack.getCurrentRound());
        for (int i = 1; i < 10; i++) {
            int previousRound = roundTrack.getCurrentRound();
            roundTrack.incrementRound();
            assertEquals(previousRound + 1, roundTrack.getCurrentRound());
        }
        roundTrack.incrementRound();
        assertEquals(10, roundTrack.getCurrentRound());
    }

    @Test
    void turnManagerTest2Players() {
        Game game = new Game(Arrays.asList("Fabio", "Luca"));
        Player nextRoundStartingPlayer = game.getPlayers().get(1);
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(nextRoundStartingPlayer, game.getTurnManager().getCurrentPlayer());
    }

    @Test
    void turnManagerTest3Players() {
        Game game = new Game(Arrays.asList("Fabio", "Luca", "Kai"));
        Player nextRoundStartingPlayer = game.getPlayers().get(1);
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(2), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(2), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(nextRoundStartingPlayer, game.getTurnManager().getCurrentPlayer());
    }

    @Test
    void turnManagerTest4Players() {
        Game game = new Game(Arrays.asList("Fabio", "Luca", "Kai", "Giovanni"));
        Player nextRoundStartingPlayer = game.getPlayers().get(1);
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(2), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(3), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(3), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(2), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(1), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(game.getPlayers().get(0), game.getTurnManager().getCurrentPlayer());
        game.getTurnManager().nextTurn();
        assertEquals(nextRoundStartingPlayer, game.getTurnManager().getCurrentPlayer());
    }

    @Test
    void waitingRoomAddPlayerTest() {
        assertTrue(WaitingRoom.getInstance().addPlayer("James"));
        assertEquals(1, WaitingRoom.getInstance().getNicknames().size());
        assertFalse(WaitingRoom.getInstance().addPlayer("James"));
        assertTrue(WaitingRoom.getInstance().addPlayer("George"));
        assertEquals(2, WaitingRoom.getInstance().getNicknames().size());
        assertFalse(WaitingRoom.getInstance().addPlayer("George"));
        assertTrue(WaitingRoom.getInstance().addPlayer("Peter"));
        assertEquals(3, WaitingRoom.getInstance().getNicknames().size());
        assertFalse(WaitingRoom.getInstance().addPlayer("Peter"));
        assertTrue(WaitingRoom.getInstance().addPlayer("John"));
        assertEquals(0, WaitingRoom.getInstance().getNicknames().size());
    }

}
