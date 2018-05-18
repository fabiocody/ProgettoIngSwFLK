package it.polimi.ingsw.server;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class SagradaServerTest {

    @Test
    void isNicknameUsedTest() {
        assertFalse(SagradaServer.getInstance().isNicknameUsed("ABC"));
        try{
            WaitingRoom.getInstance().addPlayer("ABC");
            assertTrue(SagradaServer.getInstance().isNicknameUsed("ABC"));
            WaitingRoom.getInstance().removePlayer("ABC");
        } catch (LoginFailedException e) {
            fail("Login failed");
        }
    }

}
