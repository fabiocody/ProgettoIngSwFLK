package it.polimi.ingsw;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.patterncards.WindowPattern;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WindowPatternTest {
    @Test
    public void difficultyTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();

        for (WindowPattern wp : patterns) {
            assertTrue(wp.getDifficulty() <= 6);
            assertTrue(wp.getDifficulty() >= 3);
        }
    }

    @Test
    public void patternNumberTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();

        for (WindowPattern wp : patterns) {
            assertTrue(wp.getPatternNumber() <= 23);
            assertTrue(wp.getPatternNumber() >= 0);
        }
    }

    @Test
    public void patternCardTest() {
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (int i = 0; i < patterns.size(); i += 2) {
            assertTrue(patterns.get(i).getPatternNumber()%2 == 0);
            assertTrue(patterns.get(i+1).getPatternNumber() == patterns.get(i).getPatternNumber() + 1);
        }
    }

    @Test
    public void numberOfCellsTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            assertTrue(wp.getGrid().length == 20);
        }
    }

    @Test
    public void cellValueTest(){
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
    public void cellColorTest(){
        PatternCardsGenerator gen = new PatternCardsGenerator(4);
        List<WindowPattern> patterns = gen.getCards();
        for (WindowPattern wp : patterns) {
            for(Cell c: wp.getGrid()){
                if(c.getCellColor() != null) {
                    assertTrue(c.getCellColor() instanceof Colors);
                }
            }
        }
    }

    @Test
    public void BorderConstraintTest(){
        WindowPattern pattern = new WindowPattern(42);
        PlacementConstraint con = PlacementConstraint.initialConstraint();
        Die d = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1, 6));
        List<Integer> borderPositions = Arrays.asList(0,1,2,3,4,5,9,10,14,15,16,17,18,19);
        Collections.shuffle(borderPositions);
        pattern.placeDie(d,borderPositions.get(0),con);
        for(int i = 0; i < 20; i++){
            if(i != borderPositions.get(0))
                assertTrue(pattern.getCellAt(i).getPlacedDie() == null);
        }
        assertTrue(pattern.getCellAt(borderPositions.get(0)).getPlacedDie() != null);
    }
}
