package it.polimi.ingsw.patterncards;

import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.util.Colors;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WindowPatternTest {
    @Test
     void difficultyTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getDifficulty() <= 6);
            assertTrue(wp.getDifficulty() >= 3);
        }
    }

    @Test
    void patternNumberTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getPatternNumber() <= 23);
            assertTrue(wp.getPatternNumber() >= 0);
        }
    }

    @Test
    void patternCardTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (int i = 0; i < patterns.size(); i += 2) {
            assertTrue(patterns.get(i).getPatternNumber()%2 == 0);
            assertTrue(patterns.get(i+1).getPatternNumber() == patterns.get(i).getPatternNumber() + 1);
        }
    }

    @Test
    void numberOfCellsTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getGrid().length == 20);
        }
    }

    @Test
    void cellValueTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
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
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            for(Cell c: wp.getGrid()){
                if(c.getCellColor() != null) {
                    assertTrue(c.getCellColor() instanceof Colors && c.getCellColor() != Colors.DEFAULT);
                }
            }
        }
    }
}