package it.polimi.ingsw.model.game;

import it.polimi.ingsw.server.SagradaServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class WaitingRoomTest {

    @Test
    void addPlayerTest() {
        try {
            assertNotNull(WaitingRoom.getInstance().addPlayer("James"));
            assertEquals(1, WaitingRoom.getInstance().getWaitingPlayers().size());
            assertThrows(LoginFailedException.class, () -> WaitingRoom.getInstance().addPlayer("James"));
            assertNotNull(WaitingRoom.getInstance().addPlayer("George"));
            assertEquals(2, WaitingRoom.getInstance().getWaitingPlayers().size());
            assertThrows(LoginFailedException.class, () -> WaitingRoom.getInstance().addPlayer("George"));
            assertNotNull(WaitingRoom.getInstance().addPlayer("Peter"));
            assertEquals(3, WaitingRoom.getInstance().getWaitingPlayers().size());
            assertThrows(LoginFailedException.class, () -> WaitingRoom.getInstance().addPlayer("Peter"));
            assertNotNull(WaitingRoom.getInstance().addPlayer("John"));
        } catch (LoginFailedException | NicknameAlreadyUsedInGameException e) {
            fail("Login failed");
        }
    }

    @Test
    void removePlayerTest() {
        try {
            WaitingRoom.getInstance().addPlayer("Fabio");
            Assertions.assertTrue(SagradaServer.getInstance().isNicknameUsed("Fabio"));
            WaitingRoom.getInstance().removePlayer("Fabio");
            assertFalse(SagradaServer.getInstance().isNicknameUsed("Fabio"));
        } catch (LoginFailedException | NicknameAlreadyUsedInGameException e) {
            fail("Login failed");
        }
    }

}
