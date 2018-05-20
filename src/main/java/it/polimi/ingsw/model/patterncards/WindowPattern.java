package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;

import java.util.Arrays;

public class WindowPattern {

    private final int difficulty;
    private Cell[] grid;
    private final int patternNumber;

    public WindowPattern(int patternNumber) {
        String patternID;

        if(patternNumber <0 || patternNumber > 23) {
            patternID = "WP" + 42;
            this.patternNumber = 0;
        }
        else {
            patternID = "WP" + patternNumber;
            this.patternNumber = patternNumber;
        }

        PatternValues values = PatternValues.valueOf(patternID);
        this.difficulty = values.getDifficulty();
        this.grid = new Cell[20];
        for (int i = 0; i < 20; i++) {
            grid[i] = new Cell(values.getCellColors()[i],values.getCellValues()[i]);
        }
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public Cell[] getGrid() {
        return this.grid;
    }

    public synchronized Cell getCellAt(int i){
        if(i < 0 || i > 19)
            throw new IndexOutOfBoundsException();
        return this.grid[i];
    }

    public synchronized Cell getCellAt(int i, int j){
        if(i < 0 || i > 3 || j < 0 || j > 4)
            throw new IndexOutOfBoundsException();
        return this.getCellAt(5*i + j);
    }

    public int getPatternNumber(){
        return this.patternNumber;
    }

    public boolean isGridEmpty() {
        return Arrays.stream(this.grid).noneMatch(c -> c.getPlacedDie() != null);
    }

    public synchronized void placeDie(Die d, int position, PlacementConstraint withConstraint){
        if(withConstraint.checkConstraint(this.grid, position, d))
            this.grid[position].setPlacedDie(d);
        else
            throw new InvalidPlacementException("Die " + d + " cannot be placed in position " + position);
    }

    public synchronized void placeDie(Die d, int position){
        this.placeDie(d, position, PlacementConstraint.standardConstraint());
    }

    public void moveDie(int position, int destination, PlacementConstraint withConstraint){
        Die d = this.grid[position].getPlacedDie();
        this.placeDie(d,destination,withConstraint);
        this.grid[position].setPlacedDie(null);
    }

    @Override
    public synchronized String toString() {
        String pattern = "";
        String line;

        System.out.println("Carta numero " + this.getPatternNumber());
        System.out.println("Difficoltà " + this.getDifficulty());

        for(int i=0; i < 4 ; i++){
            line="";
            for(int j = 0; j < 5; j++)
                line = line + ((this.getGrid())[5*i + j]).toString();
            line = line + "\n";
            pattern = pattern + line;
        }

        return pattern;
    }

    public void dump(){
        System.out.println(this.toString());
    }
}