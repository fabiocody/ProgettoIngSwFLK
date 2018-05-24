package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import java.util.Arrays;

/**
 * This class describes the structure and implements the method of a window pattern.
 * @author  Luca dell'Oglio
 */

public class WindowPattern {

    private final int difficulty;
    private Cell[] grid;
    private final int patternNumber;
    private final String name;

    /**
     * @param   patternNumber the number that identifies the pattern, if it's negative or bigger than 23 then the
     *          default pattern (used for testing purposes) is created
     * @see PatternValues
     * @author  Luca dell'Oglio
     */

    public WindowPattern(int patternNumber) {
        String patternID;

        if(patternNumber < 0 || patternNumber > 23) {
            patternID = "WP" + 42;
            this.patternNumber = 0;
        }
        else {
            patternID = "WP" + patternNumber;
            this.patternNumber = patternNumber;
        }

        PatternValues values = PatternValues.valueOf(patternID);
        this.difficulty = values.getDifficulty();
        this.name = values.getPatternName();
        this.grid = new Cell[20];
        for (int i = 0; i < 20; i++) {
            grid[i] = new Cell(values.getCellColors()[i],values.getCellValues()[i]);
        }
    }

    /**
     * @return  the difficulty of the pattern
     * @author  Luca dell'Oglio
     */

    public int getDifficulty() {
        return this.difficulty;
    }

    /**
     * @return  the cells of the pattern
     * @author  Luca dell'Oglio
     */

    public Cell[] getGrid() {
        return this.grid;
    }

    /**
     * @return  the name of the pattern
     * @author  Luca dell'Oglio
     */

    public String getPatternName(){return this.name;}

    /**
     * @param   i the array index
     * @return  the cell in the position indexed by <code>i</code>. The <code>grid</code> is considered a
     *          one-dimensional array
     * @throws  IndexOutOfBoundsException
     * @author  Luca dell'Oglio
     */

    public synchronized Cell getCellAt(int i){
        if(i < 0 || i > 19)
            throw new IndexOutOfBoundsException();
        return this.grid[i];
    }

    /**
     * @param   i the row index
     * @param   j the column index
     * @return  the cell in the position indexed by <code>i,j</code>. The <code>grid</code> is considered a
     *          two-dimensional array
     * @throws  IndexOutOfBoundsException
     * @author  Luca dell'Oglio
     */

    public synchronized Cell getCellAt(int i, int j){
        if(i < 0 || i > 3 || j < 0 || j > 4)
            throw new IndexOutOfBoundsException();
        return this.getCellAt(5*i + j);
    }

    /**
     * @return  the number of the pattern
     * @author  Luca dell'Oglio
     */

    public int getPatternNumber(){
        return this.patternNumber;
    }

    /**
     * @return  true if <code>grid</code> has no dice placed on it
     * @author  Luca dell'Oglio
     */

    public boolean isGridEmpty() {
        return Arrays.stream(this.grid).noneMatch(c -> c.getPlacedDie() != null);
    }

    /**
     * @param   d the die to place
     * @param   position the index that identifies the cell where to put the die on
     * @param   withConstraint the <code>placementConstraint</code> to comply
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */

    public synchronized void placeDie(Die d, int position, PlacementConstraint withConstraint){
        if(withConstraint.checkConstraint(this.grid, position, d))
            this.grid[position].setPlacedDie(d);
        else
            throw new InvalidPlacementException("Die " + d + " cannot be placed in position " + position);
    }

    /**
     * Equivalent to the previous <code>placeDie</code> but without the possibility of choosing the
     * <code>PlacementConstraint</code>: the placement is done with a <code>standardConstraint</code>
     * @param   d the die to place
     * @param   position the index that identifies the cell where to put the die on
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */

    public synchronized void placeDie(Die d, int position){
        this.placeDie(d, position, PlacementConstraint.standardConstraint());
    }

    /**
     * The movement is done by using <code>placeDie</code> on the new cell and then by removing the die from its
     * original cell
     * @param   position the index that identifies the cell where to move the die to
     * @param   destination the index that identifies the cell where the die is located
     * @param   withConstraint the <code>placementConstraint</code> to comply
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */

    public void moveDie(int position, int destination, PlacementConstraint withConstraint){
        Die d = this.grid[position].getPlacedDie();
        this.placeDie(d,destination,withConstraint);
        this.grid[position].setPlacedDie(null);
    }

    /**
     * @return  a string were each cell has proper color and value.
     * @see     Cell#toString()
     * @author  Luca dell'Oglio
     */

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        for (int k = 0; k < 5*3 + 6; k++) builder.append("-");
        builder.append("\n");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 0) builder.append("|");
                Cell cell = this.getCellAt(i, j);
                if (cell.getPlacedDie() != null) {
                    builder.append(cell.getPlacedDie().toString());
                } else {
                    builder.append(cell.toString());
                }
                builder.append("|");
            }
            builder.append("\n");
            for (int k = 0; k < 5*3 + 6; k++) builder.append("-");
            builder.append("\n");
        }
        builder.append(getPatternName());
        for (int k = getPatternName().length(); k < 20; k++)
            builder.append(" ");
        builder.append(getDifficulty());
        return builder.toString();
    }

    public void dump(){
        System.out.println("Carta numero " + this.getPatternNumber());
        System.out.println("Difficoltà " + this.getDifficulty());
        System.out.println(this.toString());
    }
}