package it.polimi.ingsw.server;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class SagradaServerTest {

    @Test
    void isNicknameUsedTest() {
        assertFalse(SagradaServer.getInstance().isNicknameUsed("Fabio"));
        WaitingRoom.getInstance().addPlayer("Fabio");
        assertTrue(SagradaServer.getInstance().isNicknameUsed("Fabio"));
    }

}
