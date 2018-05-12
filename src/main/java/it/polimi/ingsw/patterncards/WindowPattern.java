package it.polimi.ingsw.patterncards;

import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.util.*;

public class WindowPattern {

    private final int difficulty;
    private Cell[] grid;
    private final int patternNumber;

    public WindowPattern(int patternNumber) {

        switch (patternNumber) {
            case 0:
                this.patternNumber = patternNumber;
                this.difficulty = 4;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.YELLOW, null);
                grid[1] = new Cell(Colors.BLUE, null);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 1);
                grid[5] = new Cell(Colors.GREEN, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(null, 5);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, 4);
                grid[10] = new Cell(null, 3);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(Colors.RED, null);
                grid[13] = new Cell(null, null);
                grid[14] = new Cell(Colors.GREEN, null);
                grid[15] = new Cell(null, 2);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(Colors.BLUE, null);
                grid[19] = new Cell(Colors.YELLOW, null);
                break;
            case 1:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 4);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 2);
                grid[3] = new Cell(null, 5);
                grid[4] = new Cell(Colors.GREEN, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(null, 6);
                grid[8] = new Cell(Colors.GREEN, null);
                grid[9] = new Cell(null, 2);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, 3);
                grid[12] = new Cell(Colors.GREEN, null);
                grid[13] = new Cell(null, 4);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, 5);
                grid[16] = new Cell(Colors.GREEN, null);
                grid[17] = new Cell(null, 1);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
            case 2:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 5);
                grid[1] = new Cell(Colors.GREEN, null);
                grid[2] = new Cell(Colors.BLUE, null);
                grid[3] = new Cell(Colors.PURPLE, null);
                grid[4] = new Cell(null, 2);
                grid[5] = new Cell(Colors.PURPLE, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(Colors.YELLOW, null);
                grid[10] = new Cell(Colors.YELLOW, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, 6);
                grid[13] = new Cell(null, null);
                grid[14] = new Cell(Colors.PURPLE, null);
                grid[15] = new Cell(null, 1);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(Colors.GREEN, null);
                grid[19] = new Cell(null, 4);
                break;
            case 3:
                this.patternNumber = patternNumber;
                this.difficulty = 4;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.YELLOW, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 6);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 1);
                grid[7] = new Cell(null, 5);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, 2);
                grid[10] = new Cell(null, 3);
                grid[11] = new Cell(Colors.YELLOW, null);
                grid[12] = new Cell(Colors.RED, null);
                grid[13] = new Cell(Colors.PURPLE, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, 4);
                grid[18] = new Cell(null, 3);
                grid[19] = new Cell(Colors.RED, null);
                break;
            case 4:
                this.patternNumber = patternNumber;
                this.difficulty = 3;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(Colors.BLUE, null);
                grid[2] = new Cell(null, 2);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(Colors.YELLOW, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 4);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(Colors.RED, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, 5);
                grid[13] = new Cell(Colors.YELLOW, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(Colors.GREEN, null);
                grid[16] = new Cell(null, 3);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(Colors.PURPLE, null);
                break;
            case 5:
                this.patternNumber = patternNumber;
                this.difficulty = 3;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.BLUE, null);
                grid[1] = new Cell(null, 6);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(Colors.YELLOW, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 3);
                grid[7] = new Cell(Colors.BLUE, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, 5);
                grid[12] = new Cell(null, 6);
                grid[13] = new Cell(null, 2);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, 4);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, 1);
                grid[19] = new Cell(Colors.GREEN, null);
                break;
            case 6:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.PURPLE, null);
                grid[1] = new Cell(null, 6);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 3);
                grid[5] = new Cell(null, 5);
                grid[6] = new Cell(Colors.PURPLE, null);
                grid[7] = new Cell(null, 3);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, 2);
                grid[12] = new Cell(Colors.PURPLE, null);
                grid[13] = new Cell(null, 1);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, 1);
                grid[17] = new Cell(null, 5);
                grid[18] = new Cell(Colors.PURPLE, null);
                grid[19] = new Cell(null, 4);
                break;
            case 7:
                this.patternNumber = patternNumber;
                this.difficulty = 6;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 2);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 5);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 1);
                grid[5] = new Cell(Colors.YELLOW, null);
                grid[6] = new Cell(null, 6);
                grid[7] = new Cell(Colors.PURPLE, null);
                grid[8] = new Cell(null, 2);
                grid[9] = new Cell(Colors.RED, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(Colors.BLUE, null);
                grid[12] = new Cell(null, 4);
                grid[13] = new Cell(Colors.GREEN, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, 3);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, 5);
                grid[19] = new Cell(null, null);
                break;
            case 8:
                this.patternNumber = patternNumber;
                this.difficulty = 4;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.RED, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(Colors.BLUE, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(Colors.YELLOW, null);
                grid[5] = new Cell(null, 4);
                grid[6] = new Cell(Colors.PURPLE, null);
                grid[7] = new Cell(null, 3);
                grid[8] = new Cell(Colors.GREEN, null);
                grid[9] = new Cell(null, 2);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, 1);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(null, 5);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, 6);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
            case 9:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 1);
                grid[1] = new Cell(Colors.RED, null);
                grid[2] = new Cell(null, 3);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 6);
                grid[5] = new Cell(null, 5);
                grid[6] = new Cell(null, 4);
                grid[7] = new Cell(Colors.RED, null);
                grid[8] = new Cell(null, 2);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, 5);
                grid[13] = new Cell(Colors.RED, null);
                grid[14] = new Cell(null, 1);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, 3);
                grid[19] = new Cell(Colors.RED, null);
                break;
            case 10:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 6);
                grid[1] = new Cell(Colors.PURPLE, null);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 5);
                grid[5] = new Cell(null, 5);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(Colors.PURPLE, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(Colors.RED, null);
                grid[11] = new Cell(null, 6);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(Colors.PURPLE, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(Colors.YELLOW, null);
                grid[16] = new Cell(Colors.RED, null);
                grid[17] = new Cell(null, 5);
                grid[18] = new Cell(null, 4);
                grid[19] = new Cell(null, 3);
                break;
            case 11:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 6);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 5);
                grid[7] = new Cell(Colors.BLUE, null);
                grid[8] = new Cell(null, 4);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, 3);
                grid[11] = new Cell(Colors.GREEN, null);
                grid[12] = new Cell(Colors.YELLOW, null);
                grid[13] = new Cell(Colors.PURPLE, null);
                grid[14] = new Cell(null, 2);
                grid[15] = new Cell(null, 1);
                grid[16] = new Cell(null, 4);
                grid[17] = new Cell(Colors.RED, null);
                grid[18] = new Cell(null, 5);
                grid[19] = new Cell(null, 3);
                break;
            case 12:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 1);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 3);
                grid[3] = new Cell(Colors.BLUE, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 2);
                grid[7] = new Cell(Colors.BLUE, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, 6);
                grid[11] = new Cell(Colors.BLUE, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(null, 4);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(Colors.BLUE, null);
                grid[16] = new Cell(null, 5);
                grid[17] = new Cell(null, 2);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, 1);
                break;
            case 13:
                this.patternNumber = patternNumber;
                this.difficulty = 3;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, 4);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(Colors.YELLOW, null);
                grid[4] = new Cell(null, 6);
                grid[5] = new Cell(Colors.RED, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(null, 2);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(Colors.RED, null);
                grid[13] = new Cell(Colors.PURPLE, null);
                grid[14] = new Cell(null, 1);
                grid[15] = new Cell(Colors.BLUE, null);
                grid[16] = new Cell(Colors.YELLOW, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
            case 14:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, 1);
                grid[2] = new Cell(Colors.GREEN, null);
                grid[3] = new Cell(Colors.PURPLE, null);
                grid[4] = new Cell(null, 4);
                grid[5] = new Cell(null, 6);
                grid[6] = new Cell(Colors.PURPLE, null);
                grid[7] = new Cell(null, 2);
                grid[8] = new Cell(null, 5);
                grid[9] = new Cell(Colors.GREEN, null);
                grid[10] = new Cell(null, 1);
                grid[11] = new Cell(Colors.GREEN, null);
                grid[12] = new Cell(null, 5);
                grid[13] = new Cell(null, 3);
                grid[14] = new Cell(Colors.PURPLE, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
            case 15:
                this.patternNumber = patternNumber;
                this.difficulty = 4;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(Colors.GREEN, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, 2);
                grid[6] = new Cell(Colors.YELLOW, null);
                grid[7] = new Cell(null, 5);
                grid[8] = new Cell(Colors.BLUE, null);
                grid[9] = new Cell(null, 1);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(Colors.RED, null);
                grid[12] = new Cell(null, 3);
                grid[13] = new Cell(Colors.PURPLE, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, 1);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, 6);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, 4);
                break;
            case 16:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 3);
                grid[1] = new Cell(null, 4);
                grid[2] = new Cell(null, 1);
                grid[3] = new Cell(null, 5);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 6);
                grid[7] = new Cell(null, 2);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(Colors.YELLOW, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(Colors.YELLOW, null);
                grid[14] = new Cell(Colors.RED, null);
                grid[15] = new Cell(null, 5);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(Colors.YELLOW, null);
                grid[18] = new Cell(Colors.RED, null);
                grid[19] = new Cell(null, 6);
                break;
            case 17:
                this.patternNumber = patternNumber;
                this.difficulty = 3;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(Colors.RED, null);
                grid[3] = new Cell(null, 5);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(Colors.PURPLE, null);
                grid[6] = new Cell(null, 4);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(Colors.GREEN, null);
                grid[9] = new Cell(null, 3);
                grid[10] = new Cell(null, 6);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(Colors.BLUE, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(Colors.YELLOW, null);
                grid[17] = new Cell(null, 2);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
            case 18:
                this.patternNumber = patternNumber;
                this.difficulty = 6;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 6);
                grid[1] = new Cell(Colors.BLUE, null);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 1);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 5);
                grid[7] = new Cell(Colors.BLUE, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, 4);
                grid[11] = new Cell(Colors.RED, null);
                grid[12] = new Cell(null, 2);
                grid[13] = new Cell(Colors.BLUE, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(Colors.GREEN, null);
                grid[16] = new Cell(null, 6);
                grid[17] = new Cell(Colors.YELLOW, null);
                grid[18] = new Cell(null, 3);
                grid[19] = new Cell(Colors.PURPLE, null);
                break;
            case 19:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(Colors.RED, null);
                grid[4] = new Cell(null, 5);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(Colors.PURPLE, null);
                grid[8] = new Cell(null, 4);
                grid[9] = new Cell(Colors.BLUE, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(Colors.BLUE, null);
                grid[12] = new Cell(null, 3);
                grid[13] = new Cell(Colors.YELLOW, null);
                grid[14] = new Cell(null, 6);
                grid[15] = new Cell(Colors.YELLOW, null);
                grid[16] = new Cell(null, 2);
                grid[17] = new Cell(Colors.GREEN, null);
                grid[18] = new Cell(null, 1);
                grid[19] = new Cell(Colors.RED, null);
                break;
            case 20:
                this.patternNumber = patternNumber;
                this.difficulty = 6;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 1);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, 1);
                grid[6] = new Cell(Colors.GREEN, null);
                grid[7] = new Cell(null, 3);
                grid[8] = new Cell(Colors.BLUE, null);
                grid[9] = new Cell(null, 2);
                grid[10] = new Cell(Colors.BLUE, null);
                grid[11] = new Cell(null, 5);
                grid[12] = new Cell(null, 4);
                grid[13] = new Cell(null, 6);
                grid[14] = new Cell(Colors.GREEN, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(Colors.BLUE, null);
                grid[17] = new Cell(null, 5);
                grid[18] = new Cell(Colors.GREEN, null);
                grid[19] = new Cell(null, null);
                break;
            case 21:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(Colors.YELLOW, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, 2);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 6);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 4);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(null, 5);
                grid[9] = new Cell(Colors.YELLOW, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(Colors.YELLOW, null);
                grid[14] = new Cell(null, 5);
                grid[15] = new Cell(null, 1);
                grid[16] = new Cell(null, 2);
                grid[17] = new Cell(Colors.YELLOW, null);
                grid[18] = new Cell(null, 3);
                grid[19] = new Cell(null, null);
                break;
            case 22:
                this.patternNumber = patternNumber;
                this.difficulty = 6;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, 1);
                grid[1] = new Cell(Colors.PURPLE, null);
                grid[2] = new Cell(Colors.YELLOW, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, 4);
                grid[5] = new Cell(Colors.PURPLE, null);
                grid[6] = new Cell(Colors.YELLOW, null);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, 6);
                grid[10] = new Cell(Colors.YELLOW, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(null, 5);
                grid[14] = new Cell(null, 3);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, 5);
                grid[17] = new Cell(null, 4);
                grid[18] = new Cell(null, 2);
                grid[19] = new Cell(null, 1);
                break;
            case 23:
                this.patternNumber = patternNumber;
                this.difficulty = 5;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(Colors.BLUE, null);
                grid[2] = new Cell(Colors.RED, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, 4);
                grid[7] = new Cell(null, 5);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(Colors.BLUE, null);
                grid[10] = new Cell(Colors.BLUE, null);
                grid[11] = new Cell(null, 2);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(Colors.RED, null);
                grid[14] = new Cell(null, 5);
                grid[15] = new Cell(null, 6);
                grid[16] = new Cell(Colors.RED, null);
                grid[17] = new Cell(null, 3);
                grid[18] = new Cell(null, 1);
                grid[19] = new Cell(null, null);
                break;
            default:
                this.patternNumber = 0;
                this.difficulty = 0;
                this.grid = new Cell[20];
                grid[0] = new Cell(null, null);
                grid[1] = new Cell(null, null);
                grid[2] = new Cell(null, null);
                grid[3] = new Cell(null, null);
                grid[4] = new Cell(null, null);
                grid[5] = new Cell(null, null);
                grid[6] = new Cell(null, null);
                grid[7] = new Cell(null, null);
                grid[8] = new Cell(null, null);
                grid[9] = new Cell(null, null);
                grid[10] = new Cell(null, null);
                grid[11] = new Cell(null, null);
                grid[12] = new Cell(null, null);
                grid[13] = new Cell(null, null);
                grid[14] = new Cell(null, null);
                grid[15] = new Cell(null, null);
                grid[16] = new Cell(null, null);
                grid[17] = new Cell(null, null);
                grid[18] = new Cell(null, null);
                grid[19] = new Cell(null, null);
                break;
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