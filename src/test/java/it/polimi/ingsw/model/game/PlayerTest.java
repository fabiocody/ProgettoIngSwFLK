package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.objectivecards.ObjectiveCardsGenerator;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.shared.util.Constants;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.*;


class PlayerTest {

    @Test
    void playerTest() {
        Player player = new Player("James");
        List<WindowPattern> windowPatternList = new PatternCardsGenerator(2).getCardsForPlayer();
        ObjectiveCardsGenerator objectiveCardsGenerator = new ObjectiveCardsGenerator(2);
        assertThrows(IllegalStateException.class, player::getWindowPatternList);
        player.setWindowPatternList(windowPatternList);
        player.chooseWindowPattern(ThreadLocalRandom.current().nextInt(0, 2*Constants.PATTERN_CARDS_FOR_EACH_PLAYER));
        assertNotNull(player.getWindowPattern());
        assertEquals(player.getWindowPattern().getDifficulty(), player.getFavorTokens());
        assertThrows(IllegalStateException.class, () -> player.chooseWindowPattern(ThreadLocalRandom.current().nextInt(0, 2*Constants.PATTERN_CARDS_FOR_EACH_PLAYER)));
        assertThrows(IllegalStateException.class, player::getPrivateObjectiveCard);
        player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate());
        assertNotNull(player.getPrivateObjectiveCard());
        assertThrows(IllegalStateException.class, () -> player.setPrivateObjectiveCard(objectiveCardsGenerator.dealPrivate()));
    }

}
