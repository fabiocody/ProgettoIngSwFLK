package it.polimi.ingsw.placementconstraints;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.util.Colors;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementConstraintTest {
    @Test
    void PlaceDieEmptyConstraintTest(){
        PlacementConstraint con = new EmptyConstraint();
        WindowPattern pattern = new WindowPattern(42);  //default pattern
        Die d1 = new Die(Colors.getRandomColor(), ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        int index = ThreadLocalRandom.current().nextInt(0,20);
        pattern.placeDie(d1,index,con);
        assertTrue(pattern.getCellAt(index).getPlacedDie() == d1);
        assertThrows(InvalidPlacementException.class,
                ()-> pattern.placeDie(d2,index,con));
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
        //int dieValue = ThreadLocalRandom.current().nextInt(1,7);
        List<Integer> list = Constraint.validOrthogonalPositions(index);
        Die d1 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        for(int i: list){
            WindowPattern pattern = new WindowPattern(42);
            pattern.placeDie(d1, index,con1);
            assertThrows(InvalidPlacementException.class,
                    ()-> pattern.placeDie(d2,i,con2));
        }
    }
}