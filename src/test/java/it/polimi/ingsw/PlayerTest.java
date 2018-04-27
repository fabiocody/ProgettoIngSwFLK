package it.polimi.ingsw;


import it.polimi.ingsw.objectivecards.ObjectiveCardsGenerator;
import it.polimi.ingsw.server.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private static Player player = new Player("James");

    @Test
    void privateObjectiveCardTest() {
        ObjectiveCardsGenerator gen = new ObjectiveCardsGenerator(4);
        assertNull(player.getPrivateObjectiveCard());
        player.setPrivateObjectiveCard(gen.dealPrivate());
        assertNotNull(player.getPrivateObjectiveCard());
        assertThrows(IllegalStateException.class, () -> player.setPrivateObjectiveCard(gen.dealPrivate()));
    }

    @Test
    void windowPatternTest() {
        // TODO
        assertNull(player.getWindowPattern());
    }

}
