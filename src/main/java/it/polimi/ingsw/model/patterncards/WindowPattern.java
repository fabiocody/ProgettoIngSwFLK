package it.polimi.ingsw.model.patterncards;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.shared.util.Colors;
import it.polimi.ingsw.shared.util.*;
import static it.polimi.ingsw.shared.util.Constants.*;
import java.util.Arrays;
import java.io.*;


/**
 * This class describes the structure and implements the methods of a window pattern.
 * @author  Luca dell'Oglio
 */
public class WindowPattern {

    private String name;
    private int difficulty;
    private int patternNumber;
    private Cell[] grid;

    /**
     * @param   patternNumber the number that identifies the pattern, if it's negative or bigger than 23 then the
     *          default pattern (used for testing purposes) is created
     * @author  Luca dell'Oglio
     */
    public WindowPattern(int patternNumber) {
        String patternID = "windowPattern";
        if (patternNumber < 0 || patternNumber >= NUMBER_OF_PATTERNS)
            this.patternNumber = NUMBER_OF_PATTERNS;
        else
            this.patternNumber = patternNumber;
        patternID = patternID + this.patternNumber;
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/WindowPatterns.json"), "UTF-8")) {
            JsonParser parser = new JsonParser();
            JsonObject pattern = parser.parse(reader).getAsJsonObject().get(patternID).getAsJsonObject();
            this.name = pattern.get(JsonFields.NAME).getAsString();
            this.difficulty = pattern.get(JsonFields.DIFFICULTY).getAsInt();
            JsonArray jsonGrid = pattern.get(JsonFields.GRID).getAsJsonArray();
            this.grid = new Cell[NUMBER_OF_PATTERN_COLUMNS * NUMBER_OF_PATTERN_ROWS];
            for (int i = 0; i < NUMBER_OF_PATTERN_COLUMNS *NUMBER_OF_PATTERN_ROWS; i++) {
                JsonObject cell = jsonGrid.get(i).getAsJsonObject();
                Colors cellColor = cell.get(JsonFields.COLOR) != JsonNull.INSTANCE ? Colors.valueOf(cell.get(JsonFields.COLOR).getAsString()) : null;
                Integer cellValue = cell.get(JsonFields.VALUE) != JsonNull.INSTANCE ? cell.get(JsonFields.VALUE).getAsInt() : null;
                this.grid[i] = new Cell(cellColor,cellValue);
            }
        } catch (IOException e) {
            Logger.printStackTrace(e);
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
    public String getPatternName() {
        return this.name;
    }

    /**
     * @param   i the array index
     * @return  the cell in the position indexed by <code>i</code>. The <code>grid</code> is considered a
     *          one-dimensional array
     * @throws  IndexOutOfBoundsException
     * @author  Luca dell'Oglio
     */
    public Cell getCell(int i) {
        if (!(i >= 0 && i < NUMBER_OF_PATTERN_COLUMNS *NUMBER_OF_PATTERN_ROWS))
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
    public Cell getCell(int i, int j) {
        if (!(i >= 0 && i < NUMBER_OF_PATTERN_ROWS && j >= 0 && j < NUMBER_OF_PATTERN_COLUMNS))
            throw new IndexOutOfBoundsException();
        return this.getCell(NUMBER_OF_PATTERN_COLUMNS *i + j);
    }

    /**
     * @return  the number of the pattern
     * @author  Luca dell'Oglio
     */
    int getPatternNumber(){
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
     * @return  true if <code>grid</code> contains only one die
     * @author  Luca dell'Oglio
     */
    public boolean checkIfOnlyOneDie() {
        return 1 == Arrays.stream(this.grid).filter(c -> c.getPlacedDie() != null).count();
    }


    /**
     * @param   d the die to place
     * @param   position the index that identifies the cell where to put the die on
     * @param   constraint the <code>placementConstraint</code> to comply
     * @throws  InvalidPlacementException when placement is invalid
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */
    public void placeDie(Die d, int position, PlacementConstraint constraint) {
        if (position < this.grid.length && constraint.checkConstraint(this.grid, position, d))
            this.grid[position].setPlacedDie(d);
        else
            throw new InvalidPlacementException(InterfaceMessages.DIE_INVALID_POSITION);
    }

    /**
     * Equivalent to the previous <code>placeDie</code> but without the possibility of choosing the
     * <code>PlacementConstraint</code>: the placement is done with a <code>standardConstraint</code>
     * @param   d the die to place
     * @param   position the index that identifies the cell where to put the die on
     * @throws  InvalidPlacementException when placement is invalid
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */
    public void placeDie(Die d, int position) {
        if (this.isGridEmpty())
            this.placeDie(d, position, PlacementConstraint.initialConstraint());
        else
            this.placeDie(d, position, PlacementConstraint.standardConstraint());
    }

    /**
     * The movement is done by using <code>placeDie</code> on the new cell and then by removing the die from its
     * original cell
     * @param   position the index that identifies the cell where to move the die to
     * @param   destination the index that identifies the cell where the die is located
     * @param   constraint the <code>placementConstraint</code> to comply
     * @see     PlacementConstraint
     * @author  Luca dell'Oglio
     */
    public void moveDie(int position, int destination, PlacementConstraint constraint) {
        Die d = this.grid[position].getPlacedDie();
        this.grid[position].setPlacedDie(null);
        try {
            this.placeDie(d, destination, constraint);
        } catch (InvalidPlacementException e) {
            this.grid[position].setPlacedDie(d);
            throw e;
        }
    }

    /**
     * @return  a string were each cell has proper color and value.
     * @see     Cell#toString()
     * @author  Luca dell'Oglio
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int k = 0; k <= NUMBER_OF_PATTERN_ROWS *NUMBER_OF_PATTERN_COLUMNS; k++) builder.append("-");
        builder.append("\n");
        for (int i = 0; i < NUMBER_OF_PATTERN_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_PATTERN_COLUMNS; j++) {
                if (j == 0) builder.append("|");
                Cell cell = this.getCell(i, j);
                if (cell.getPlacedDie() != null) {
                    builder.append(cell.getPlacedDie().toString());
                } else {
                    builder.append(cell.toString());
                }
                builder.append("|");
            }
            builder.append("\n");
            for (int k = 0; k <= NUMBER_OF_PATTERN_ROWS *NUMBER_OF_PATTERN_COLUMNS; k++) builder.append("-");
            builder.append("\n");
        }
        builder.append(getPatternName());
        for (int k = getPatternName().length(); k < NUMBER_OF_PATTERN_ROWS *NUMBER_OF_PATTERN_COLUMNS; k++)
            builder.append(" ");
        builder.append(getDifficulty());
        return builder.toString();
    }

}