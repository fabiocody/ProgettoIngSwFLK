package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import org.junit.jupiter.api.Test;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TurnManagerTest {

    @Test
    void turnManagerTest2Players() {
        Game game = new Game(Stream.of("Fabio", "Luca")
                .map(Player::new)
                .collect(Collectors.toList())
        );
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
        Game game = new Game(Stream.of("Fabio", "Luca", "Kai")
                .map(Player::new)
                .collect(Collectors.toList())
        );
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
        Game game = new Game(Stream.of("Fabio", "Luca", "Kai", "Jason")
                .map(Player::new)
                .collect(Collectors.toList())
        );
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
