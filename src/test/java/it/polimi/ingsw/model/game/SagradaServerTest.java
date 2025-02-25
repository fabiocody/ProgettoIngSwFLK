package it.polimi.ingsw.model.game;

import it.polimi.ingsw.server.SagradaServer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class SagradaServerTest {

    @Test
    void isNicknameUsedTest() {
        Assertions.assertFalse(SagradaServer.getInstance().isNicknameUsed("ABC"));
        try{
            WaitingRoom.getInstance().addPlayer("ABC");
            assertTrue(SagradaServer.getInstance().isNicknameUsed("ABC"));
            WaitingRoom.getInstance().removePlayer("ABC");
        } catch (LoginFailedException | NicknameAlreadyUsedInGameException e) {
            fail("Login failed");
        }
    }

}
