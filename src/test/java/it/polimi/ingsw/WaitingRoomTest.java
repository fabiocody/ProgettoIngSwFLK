package it.polimi.ingsw;

import it.polimi.ingsw.server.WaitingRoom;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class WaitingRoomTest {

    @Test
    void addPlayerTest() {
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
