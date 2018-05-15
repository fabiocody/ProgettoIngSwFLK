package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.util.*;


public class Cell {
    private final Colors cellColor;
    private final Integer cellValue;
    private Die placedDie;

    public Cell(Colors cellColor, Integer cellValue){
        this.cellColor = cellColor;
        this.cellValue = cellValue;
    }

    public Colors getCellColor(){
        return this.cellColor;
    }

    public Integer getCellValue(){
        return this.cellValue;
    }

    public synchronized Die getPlacedDie() {
        return placedDie;
    }

    synchronized void setPlacedDie(Die placedDie) {
        this.placedDie = placedDie;
    }

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

