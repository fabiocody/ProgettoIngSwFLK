package it.polimi.ingsw;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.util.Colors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
                    assertTrue(c.getCellColor() instanceof Colors && c.getCellColor() != Colors.RESET);
                }
            }
        }
    }

    @Test
    void PlaceDieEmptyConstraintTest(){
        PlacementConstraint con = new EmptyConstraint();
        WindowPattern pattern = new WindowPattern(42);  //default pattern
        Die d1 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        int index = ThreadLocalRandom.current().nextInt(0,20);
        pattern.placeDie(d1,index,con);
        assertTrue(pattern.getCellAt(index).getPlacedDie() == d1);
        assertThrows(InvalidPlacementException.class,
                ()->{
                    pattern.placeDie(d2,index,con);
                });
    }


    @Test
    void BorderConstraintTest(){
        PlacementConstraint con = new BorderConstraint(new EmptyConstraint());
        Die d = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        for(int i = 0; i < 2; i++){
            WindowPattern pattern = new WindowPattern(42);                      //default pattern
            WindowPattern pattern2 = new WindowPattern(42);
            int index = ThreadLocalRandom.current().nextInt(0,5) + 15*i;
            int index2 = 5*ThreadLocalRandom.current().nextInt(0,4) + 4*i;
            pattern.placeDie(d,index,con);
            pattern2.placeDie(d,index2,con);
            assertTrue(pattern.getCellAt(index).getPlacedDie() != null);
            assertTrue(pattern2.getCellAt(index2).getPlacedDie() != null);
        }
    }

    @Test
    void ColorConstraintTest(){
        PlacementConstraint con = new ColorConstraint(new EmptyConstraint());
        WindowPattern pattern = new WindowPattern(ThreadLocalRandom.current().nextInt(0,24));
        int j;
        do{
            j = ThreadLocalRandom.current().nextInt(0,20);
        } while (pattern.getCellAt(j).getCellColor() == null);
        Die d = new Die(pattern.getCellAt(j).getCellColor(),ThreadLocalRandom.current().nextInt(1,7));
        pattern.placeDie(d,j,con);
        assertTrue(pattern.getCellAt(j).getCellColor() == d.getColor());
    }

    @Test
    void ValueConstraintTest(){
        PlacementConstraint con = new ValueConstraint(new EmptyConstraint());
        WindowPattern pattern = new WindowPattern(ThreadLocalRandom.current().nextInt(0,24));
        int j;
        do{
            j = ThreadLocalRandom.current().nextInt(0,20);
        } while (pattern.getCellAt(j).getCellValue() == null);
        Die d = new Die(Colors.getRandomColor(),pattern.getCellAt(j).getCellValue());
        pattern.placeDie(d,j,con);
        assertTrue(pattern.getCellAt(j).getCellValue() == d.getValue());
    }

    @Test
    void PositionConstraintTest(){
        PlacementConstraint con1 = new EmptyConstraint();
        PlacementConstraint con2 = new PositionConstraint(con1);
        int index = ThreadLocalRandom.current().nextInt(0,20);
        List<Integer> list = Constraint.validPositions(index);
        Die d1 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        for(int i: list){
            WindowPattern pattern = new WindowPattern(42);
            pattern.placeDie(d1, index,con1);
            pattern.placeDie(d2,i,con2);
            assertTrue(pattern.getCellAt(i).getPlacedDie() == d2);
        }
    }

    @Test
    void OrthogonalConstraintTest(){
        PlacementConstraint con1 = new EmptyConstraint();
        PlacementConstraint con2 = new OrthogonalConstraint(new EmptyConstraint());
        int index = ThreadLocalRandom.current().nextInt(0,20);
        Colors color = Colors.getRandomColor();
        int dieValue = ThreadLocalRandom.current().nextInt(1,7);
        List<Integer> list = Constraint.validOrthogonalPositions(index);
        Die d1 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        for(int i: list){
            WindowPattern pattern = new WindowPattern(42);
            pattern.placeDie(d1, index,con1);
            assertThrows(InvalidPlacementException.class,
                    ()->{
                        pattern.placeDie(d2,i,con2);
                    });
        }
    }
}

