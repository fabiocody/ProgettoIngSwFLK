package it.polimi.ingsw.patterncards;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.util.*;


public class Cell {
    private Colors cellColor;
    private Integer cellValue;
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

    public Die getPlacedDie() {
        return placedDie;
    }

    void setPlacedDie(Die placedDie) {
        this.placedDie = placedDie;
    }

    @Override
    public String toString() {
        String cell ="";
        if (this.cellColor != null && this.cellValue != null)
            cell = this.cellColor.escape() + this.cellValue + " " + Colors.RESET.escape();
        else if (this.cellColor == null && this.cellValue != null)
            cell = this.cellValue + " " + Colors.RESET.escape();
        else if (this.cellColor != null && this.cellValue == null)
            cell = this.cellColor.escape() + "0 " + Colors.RESET.escape();
        else if (this.cellColor == null && this.cellValue == null)
            cell = "0 " +Colors.RESET.escape();
        return cell;
    }

    public void dump(){
        System.out.println(this.toString());
    }

}

