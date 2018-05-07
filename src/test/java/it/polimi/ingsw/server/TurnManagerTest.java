package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TurnManagerTest {

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

}
