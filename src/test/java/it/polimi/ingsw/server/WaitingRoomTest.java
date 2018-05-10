package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class WaitingRoomTest {

    @Test
    void waitingRoomAddPlayerTest() {
        assertNotNull(WaitingRoom.getInstance().addPlayer("James"));
        assertEquals(1, WaitingRoom.getInstance().getWaitingPlayers().size());
        assertNull(WaitingRoom.getInstance().addPlayer("James"));
        assertNotNull(WaitingRoom.getInstance().addPlayer("George"));
        assertEquals(2, WaitingRoom.getInstance().getWaitingPlayers().size());
        assertNull(WaitingRoom.getInstance().addPlayer("George"));
        assertNotNull(WaitingRoom.getInstance().addPlayer("Peter"));
        assertEquals(3, WaitingRoom.getInstance().getWaitingPlayers().size());
        assertNull(WaitingRoom.getInstance().addPlayer("Peter"));
        assertNotNull(WaitingRoom.getInstance().addPlayer("John"));
        assertEquals(0, WaitingRoom.getInstance().getWaitingPlayers().size());
    }

}
