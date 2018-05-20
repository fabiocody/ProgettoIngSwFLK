package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.util.*;

/**
 * This class describes a pattern cell.
 * @author  Luca dell'Oglio
 */

public class Cell {
    private final Colors cellColor;
    private final Integer cellValue;
    private Die placedDie;

    /**
     * @param   cellColor the color of the cell (can be null)
     * @param   cellValue the value of the cell (can be null)
     * @author  Luca dell'Oglio
     */

    public Cell(Colors cellColor, Integer cellValue){
        this.cellColor = cellColor;
        this.cellValue = cellValue;
    }

    /**
     * @return  the color of the cell
     * @author  Luca dell'Oglio
     */

    public Colors getCellColor(){
        return this.cellColor;
    }

    /**
     * @return  the value of the cell
     * @author  Luca dell'Oglio
     */

    public Integer getCellValue(){
        return this.cellValue;
    }

    /**
     * @return  the die placed on the cell
     * @see     WindowPattern
     * @author  Luca dell'Oglio
     */

    public synchronized Die getPlacedDie() {
        return placedDie;
    }

    /**
     * @param   placedDie the die to be placed on the cell
     * @see     WindowPattern
     * @author  Luca dell'Oglio
     */

    synchronized void setPlacedDie(Die placedDie) {
        this.placedDie = placedDie;
    }

    /**
     * @return  a string were the cell is represented with the proper color. If the cell has null value then the value
     *          printed will be 0, if the cell has null color its value will be printed in white.
     * @author  Luca dell'Oglio
     */

    @Override
    public synchronized String toString() {
        if (this.cellColor != null && this.cellValue != null)
            return Colorify.colorify(this.cellValue + " ", this.cellColor);
        else if (this.cellColor == null && this.cellValue != null)
            return Colorify.colorify(this.cellValue + " ", Colors.RESET);
        else if (this.cellColor != null)
            return Colorify.colorify("0 ", this.cellColor);
        else
            return Colorify.colorify("0 ", Colors.RESET);
    }

    public void dump(){
        System.out.println(this.toString());
    }

}

