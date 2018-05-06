package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.ObjectiveCardsGenerator;
import it.polimi.ingsw.patterncards.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class PlayerTest {

    @Test
    void playerTest() {
        Player player = new Player("James");
        WindowPattern window = new PatternCardsGenerator(2).getCard().get(0);
        ObjectiveCardsGenerator objectiveCardsGenerator = new ObjectiveCardsGenerator(2);
        assertThrows(IllegalStateException.class, player::getWindowPattern);
        player.setWindowPattern(window);
        assertNotNull(player.getWindowPattern());
        assertEquals(window.getDifficulty(), player.getFavorTokens());
        assertThrows(IllegalStateException.class, () -> player.setWindowPattern(window));
        assertThrows(IllegalStateException.class, player::getPrivateObjectiveCard);
        player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate());
        assertNotNull(player.getPrivateObjectiveCard());
        assertThrows(IllegalStateException.class, () -> player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate()));
    }

}
