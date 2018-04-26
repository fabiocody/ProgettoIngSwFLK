package it.polimi.ingsw;

import it.polimi.ingsw.server.WaitingRoom;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class WaitingRoomTest {

    private static WaitingRoom waitingRoom;

    @BeforeAll
    static void setup() {
        waitingRoom = new WaitingRoom();
    }

    @Test
    void addPlayerTest() {
        assertTrue(waitingRoom.addPlayer("James"));
        assertEquals(1, waitingRoom.getNicknames().size());
        assertFalse(waitingRoom.addPlayer("James"));
        assertTrue(waitingRoom.addPlayer("George"));
        assertEquals(2, waitingRoom.getNicknames().size());
        assertFalse(waitingRoom.addPlayer("George"));
        assertTrue(waitingRoom.addPlayer("Peter"));
        assertEquals(3, waitingRoom.getNicknames().size());
        assertFalse(waitingRoom.addPlayer("Peter"));
        assertTrue(waitingRoom.addPlayer("John"));
        assertEquals(0, waitingRoom.getNicknames().size());
    }

}
