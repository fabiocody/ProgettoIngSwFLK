package it.polimi.ingsw.model.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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

    @Test
    void countSuspendedPlayersTest() {
        List<String> nicknames = Arrays.asList("aaa", "bbb", "ccc");
        List<Player> players = nicknames.stream()
                .map(Player::new)
                .collect(Collectors.toList());
        TurnManager turnManager = new TurnManager(players);
        players.get(ThreadLocalRandom.current().nextInt(0, players.size())).setSuspended(true);
        assertEquals(2, turnManager.countNotSuspendedPlayers());
        players.forEach(player -> player.setSuspended(true));
        assertEquals(0, turnManager.countNotSuspendedPlayers());
        players.forEach(player -> player.setSuspended(false));
        assertEquals(3, turnManager.countNotSuspendedPlayers());
    }

}
