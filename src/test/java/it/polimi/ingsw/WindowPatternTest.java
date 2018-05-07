package it.polimi.ingsw;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.Cell;
import it.polimi.ingsw.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.patterncards.WindowPattern;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.Test;

import java.awt.*;
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
        PlacementConstraint con = PlacementConstraint.initialConstraint();
        Die d = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,6));
        for(int i = 0; i < 2; i++){
            WindowPattern pattern = new WindowPattern(42);                      //default pattern
            WindowPattern pattern2 = new WindowPattern(42);
            int index = ThreadLocalRandom.current().nextInt(0,4) + 15*i;
            int index2 = 5*ThreadLocalRandom.current().nextInt(0,3) + 4*i;
            pattern.placeDie(d,index,con);
            pattern2.placeDie(d,index,con);
            assertTrue(pattern.getCellAt(index).getPlacedDie() != null);
            assertTrue(pattern2.getCellAt(index).getPlacedDie() != null);
        }
    }

    @Test
    public void ColorConstraintTest(){
        WindowPattern pattern;
        PlacementConstraint con = PlacementConstraint.initialConstraint();
        for(int i = 0; i < 24; i++) {
            pattern = new WindowPattern(i);
            List<Integer> list = Arrays.asList(0,1,2,3,4,5,9,10,14,15,16,17,18,19);
            int j = 0;
            while(pattern.getCellAt(list.get(j)).getCellColor() == null ){
                j++;
            }
            Die d = new Die(pattern.getCellAt(list.get(j)).getCellColor(), ThreadLocalRandom.current().nextInt(1, 6));
            pattern.placeDie(d, list.get(j), con);
            assertTrue(pattern.getCellAt(list.get(j)).getCellColor() == d.getColor());
        }
    }
}
