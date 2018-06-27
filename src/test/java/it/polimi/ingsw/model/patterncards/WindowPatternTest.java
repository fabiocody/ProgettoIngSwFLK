package it.polimi.ingsw.model.patterncards;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import it.polimi.ingsw.model.Colors;
import it.polimi.ingsw.model.dice.*;
import it.polimi.ingsw.model.placementconstraints.EmptyConstraint;
import it.polimi.ingsw.shared.util.Constants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WindowPatternTest {
    @Test
     void difficultyTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getDifficulty() <= 6);
            assertTrue(wp.getDifficulty() >= 3);
        }
    }

    @Test
    void patternNumberTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getPatternNumber() < Constants.NUMBER_OF_PATTERNS);
            assertTrue(wp.getPatternNumber() >= 0);
        }
    }

    @Test
    void patternCardTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (int i = 0; i < patterns.size(); i += 2) {
            assertTrue(patterns.get(i).getPatternNumber()%2 == 0);
            assertTrue(patterns.get(i+1).getPatternNumber() == patterns.get(i).getPatternNumber() + 1);
        }
    }

    @Test
    void numberOfCellsTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getGrid().length == Constants.NUMBER_OF_PATTERN_COLUMNS*Constants.NUMBER_OF_PATTERN_ROWS);
        }
    }

    @Test
    void cellValueTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            for(Cell c: wp.getGrid()) {
                if (c.getCellValue() != null) {
                    assertTrue(c.getCellValue() <= 6);
                    assertTrue(c.getCellValue() >= 1);
                }
            }
        }
    }

    @Test
    void cellColorTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            for(Cell c: wp.getGrid()){
                if(c.getCellColor() != null) {
                    assertTrue(c.getCellColor() instanceof Colors && c.getCellColor() != Colors.DEFAULT);
                }
            }
        }
    }

    @Test
    void isGridEmptyTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        DiceGenerator dg = new DiceGenerator(Constants.MAX_NUMBER_OF_PLAYERS);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns){
            assertTrue(wp.isGridEmpty());
            Die d = dg.draw();
            wp.placeDie(d, ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS*Constants.NUMBER_OF_PATTERN_ROWS),new EmptyConstraint());
            assertFalse(wp.isGridEmpty());
        }
    }
}