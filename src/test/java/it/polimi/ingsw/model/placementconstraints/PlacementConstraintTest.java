package it.polimi.ingsw.model.placementconstraints;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import it.polimi.ingsw.model.Colors;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.shared.util.Constants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class PlacementConstraintTest {

    @Test
    void PlaceDieEmptyConstraintTest(){
        PlacementConstraint con = new EmptyConstraint();
        WindowPattern pattern = new WindowPattern(Constants.INDEX_CONSTANT);
        Die d1 = new Die(Colors.getRandomColor(), ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        int index = ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS);
        pattern.placeDie(d1,index,con);
        assertTrue(pattern.getCell(index).getPlacedDie() == d1);
        assertThrows(InvalidPlacementException.class,
                ()-> pattern.placeDie(d2,index,con));
    }

    @Test
    void BorderConstraintTest(){
        PlacementConstraint con = new BorderConstraint(new EmptyConstraint());
        Die d = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        for(int i = 0; i < 2; i++){
            WindowPattern pattern = new WindowPattern(Constants.INDEX_CONSTANT);
            WindowPattern pattern2 = new WindowPattern(Constants.INDEX_CONSTANT);
            int index = ThreadLocalRandom.current().nextInt(0,5) + 15*i;
            int index2 = 5*ThreadLocalRandom.current().nextInt(0,4) + 4*i;
            pattern.placeDie(d,index,con);
            pattern2.placeDie(d,index2,con);
            assertTrue(pattern.getCell(index).getPlacedDie() != null);
            assertTrue(pattern2.getCell(index2).getPlacedDie() != null);
        }
    }

    @Test
    void ColorConstraintTest(){
        PlacementConstraint con = new ColorConstraint(new EmptyConstraint());
        WindowPattern pattern = new WindowPattern(ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERNS));
        int j;
        do{
            j = ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS);
        } while (pattern.getCell(j).getCellColor() == null);
        Die d = new Die(pattern.getCell(j).getCellColor(),ThreadLocalRandom.current().nextInt(1,7));
        pattern.placeDie(d,j,con);
        assertTrue(pattern.getCell(j).getCellColor() == d.getColor());
    }

    @Test
    void ValueConstraintTest(){
        PlacementConstraint con = new ValueConstraint(new EmptyConstraint());
        WindowPattern pattern = new WindowPattern(ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERNS));
        int j;
        do{
            j = ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS);
        } while (pattern.getCell(j).getCellValue() == null);
        Die d = new Die(Colors.getRandomColor(),pattern.getCell(j).getCellValue());
        pattern.placeDie(d,j,con);
        assertTrue(pattern.getCell(j).getCellValue() == d.getValue());
    }

    @Test
    void PositionConstraintTest(){
        PlacementConstraint con1 = new EmptyConstraint();
        PlacementConstraint con2 = new PositionConstraint(con1);
        int index = ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS);
        List<Integer> list = Constraint.validPlacementPositions(index);
        Die d1 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(Colors.getRandomColor(),ThreadLocalRandom.current().nextInt(1,7));
        for(int i: list){
            WindowPattern pattern = new WindowPattern(Constants.INDEX_CONSTANT);
            pattern.placeDie(d1, index,con1);
            pattern.placeDie(d2,i,con2);
            assertTrue(pattern.getCell(i).getPlacedDie() == d2);
        }
    }

    @Test
    void OrthogonalConstraintTest(){
        PlacementConstraint con1 = new EmptyConstraint();
        PlacementConstraint con2 = new OrthogonalConstraint(new EmptyConstraint());
        int index = ThreadLocalRandom.current().nextInt(0,Constants.NUMBER_OF_PATTERN_COLUMNS *Constants.NUMBER_OF_PATTERN_ROWS);
        Colors color = Colors.getRandomColor();
        List<Integer> list = Constraint.validOrthogonalPositions(index);
        Die d1 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        Die d2 = new Die(color,ThreadLocalRandom.current().nextInt(1,7));
        for(int i: list){
            WindowPattern pattern = new WindowPattern(Constants.INDEX_CONSTANT);
            pattern.placeDie(d1, index,con1);
            assertThrows(InvalidPlacementException.class,
                    ()-> pattern.placeDie(d2,i,con2));
        }
    }
}