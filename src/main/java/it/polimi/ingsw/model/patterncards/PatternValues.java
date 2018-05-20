package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.util.Colors;

public enum PatternValues {

    WP0(4,  new Integer[]{  null,null,null,null,1,null,null,5,null,4,3,null,null,null,null,2,null,null,null,null},
            new Colors[]{   Colors.YELLOW,Colors.BLUE,null,null,null,Colors.GREEN,null,null,null,null,
                            null,null, Colors.RED,null,Colors.GREEN,null,null,null,Colors.BLUE,Colors.YELLOW}),
    WP1(5,  new Integer[]{  4,null,2,5,null,null,null,6,null,2,null,3,null,4,null,5,null,1,null,null},
            new Colors[]{   null,null,null,null,Colors.GREEN,null,null,null,Colors.GREEN,null,
                            null,null,Colors.GREEN,null,null,null,Colors.GREEN,null,null,null}),
    WP2(5,  new Integer[]{  5,null,null,null,2,null,null,null,null,null,null,null,6,null,null,1,null,null,null,4},
            new Colors[]{   null,Colors.GREEN,Colors.BLUE,Colors.PURPLE,null,Colors.PURPLE,null,null,null,Colors.YELLOW,
                            Colors.YELLOW,null,null,null,Colors.PURPLE,null,null,null,Colors.GREEN,null}),
    WP3(4,  new Integer[]{  null,null,6,null,null,null,1,5,null,2,3,null,null,null,null,null,null,4,3,null},
            new Colors[]{   Colors.YELLOW,null,null,null,null,null,null,null,null,null,
                            null,Colors.YELLOW,Colors.RED,Colors.PURPLE,null,null,null,null,null,Colors.RED}),
    WP4(3,  new Integer[]{  null,null,2,null,null,null,4,null,null,null,null,null,5,null,null,null,3,null,null,null},
            new Colors[]{   null,Colors.BLUE,null,null,Colors.YELLOW,null,null,null,Colors.RED,null,
                            null,null,null,Colors.YELLOW,null,Colors.GREEN,null,null,null,Colors.PURPLE}),
    WP5(3,  new Integer[]{  null,6,null,null,null,null,3,null,null,null,null,5,6,2,null,null,4,null,1,null},
            new Colors[]{   Colors.BLUE,null,null,null,Colors.YELLOW,null,null,Colors.BLUE,null,null,
                            null,null,null,null,null,null,null,null,null,Colors.GREEN}),
    WP6(5,  new Integer[]{  null,6,null,null,3,5,null,3,null,null,null,2,null,1,null,null,1,5,null,4},
            new Colors[]{   Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null,null,null,
                            null,null,Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null}),
    WP7(6,  new Integer[]{  2,null,5,null,1,null,6,null,2,null,null,null,4,null,null,null,3,null,5,null},
            new Colors[]{   null,null,null,null,null,Colors.YELLOW,null,Colors.PURPLE,null,Colors.RED,
                            null,Colors.BLUE,null,Colors.GREEN,null,null,null,null,null,null}),
    WP8(4,  new Integer[]{  null,null,null,null,null,4,null,3,null,2,null,1,null,5,null,null,null,6,null,null},
            new Colors[]{   Colors.RED,null,Colors.BLUE,null,Colors.YELLOW,null,Colors.PURPLE,null,Colors.GREEN,null,
                            null,null,null,null,null,null,null,null,null,null}),
    WP9(5,  new Integer[]{  1,null,3,null,6,5,4,null,2,null,null,null,5,null,1,null,null,null,3,null},
            new Colors[]{   null,Colors.RED,null,null,null,null,null,Colors.RED,null,null,
                            null,null,null,Colors.RED,null,null,null,null,null,Colors.RED}),
    WP10(5, new Integer[]{  6,null,null,null,5,5,null,null,null,null,null,6,null,null,null,null,null,5,4,3},
            new Colors[]{   null,Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null,null,
                            Colors.RED,null,null,Colors.PURPLE,null,Colors.YELLOW,Colors.RED,null,null,null}),
    WP11(5, new Integer[]{  null,null,6,null,null,null,5,null,4,null,3,null,null,null,2,1,4,null,5,3},
            new Colors[]{   null,null,null,null,null,null,null,Colors.BLUE,null,null,
                            null,Colors.GREEN,Colors.YELLOW,Colors.PURPLE,null,null,null,Colors.RED,null,null}),
    WP12(5, new Integer[]{  1,null,3,null,null,null,2,null,null,null,6,null,null,4,null,null,5,2,null,1},
            new Colors[]{   null,null,null,Colors.BLUE,null,null,null,Colors.BLUE,null,null,
                            null,Colors.BLUE,null,null,null,Colors.BLUE,null,null,null,null}),
    WP13(3, new Integer[]{  null,4,null,null,6,null,null,2,null,null,null,null,null,null,1,null,null,null,null,null},
            new Colors[]{   null,null,null,Colors.YELLOW,null,Colors.RED,null,null,null,null,
                            null,null,Colors.RED,Colors.PURPLE,null,Colors.BLUE,Colors.YELLOW,null,null,null}),
    WP14(5, new Integer[]{  null,1,null,null,4,6,null,2,5,null,1,null,5,3,null,null,null,null,null,null},
            new Colors[]{   null,null,Colors.GREEN,Colors.PURPLE,null,null,Colors.PURPLE,null,null,Colors.GREEN,
                            null,Colors.GREEN,null,null,Colors.PURPLE,null,null,null,null,null}),
    WP15(4, new Integer[]{  null,null,null,null,null,2,null,5,null,1,null,null,3,null,null,1,null,6,null,4},
            new Colors[]{   null,null,Colors.GREEN,null,null,null,Colors.YELLOW,null,Colors.BLUE,null,
                    null,Colors.RED,null,Colors.PURPLE,null,null,null,null,null,null}),
    WP16(5, new Integer[]{  3,4,1,5,null,null,6,2,null,null,null,null,null,null,null,5,null,null,null,6},
            new Colors[]{   null,null,null,null,null,null,null,null,null,Colors.YELLOW,
                            null,null,null,Colors.YELLOW,Colors.RED,null,null,Colors.YELLOW,Colors.RED,null}),
    WP17(3, new Integer[]{  null,null,null,5,null,null,4,null,null,3,6,null,null,null,null,null,null,2,null,null},
            new Colors[]{   null,null,Colors.RED,null,null,Colors.PURPLE,null,null,Colors.GREEN,null,
                            null,null,null,Colors.BLUE,null,null,Colors.YELLOW,null,null,null}),
    WP18(6, new Integer[]{  6,null,null,null,1,null,5,null,null,null,4,null,2,null,null,null,6,null,3,null},
            new Colors[]{   null,Colors.BLUE,null,null,null,null,null,Colors.BLUE,null,null,
                            null,Colors.RED,null,Colors.BLUE,null,Colors.GREEN,null,Colors.YELLOW,null,Colors.PURPLE}),
    WP19(5, new Integer[]{  null,null,null,null,5,null,null,null,4,null,null,null,3,null,6,null,2,null,1,null},
            new Colors[]{   null,null,null,Colors.RED,null,null,null,Colors.PURPLE,null,Colors.BLUE,
                            null,Colors.BLUE,null,Colors.YELLOW,null,Colors.YELLOW,null,Colors.GREEN,null,Colors.RED}),
    WP20(6, new Integer[]{  null,null,1,null,null,1,null,3,null,2,null,5,4,6,null,null,null,5,null,null},
            new Colors[]{   null,null,null,null,null,null,Colors.GREEN,null,Colors.BLUE,null,
                            Colors.BLUE,null,null,null,Colors.GREEN,null,Colors.BLUE,null,Colors.GREEN,null}),
    WP21(5, new Integer[]{  null,null,2,null,6,null,4,null,5,null,null,null,null,null,5,1,2,null,3,null},
            new Colors[]{   Colors.YELLOW,null,null,null,null,null,null,null,null,Colors.YELLOW,
                            null,null,null,Colors.YELLOW,null,null,null,Colors.YELLOW,null,null}),
    WP22(6, new Integer[]{  1,null,null,null,4,null,null,null,null,6,null,null,null,5,3,null,5,4,2,1},
            new Colors[]{   null,Colors.PURPLE,Colors.YELLOW,null,null,Colors.PURPLE,Colors.YELLOW,null,null,null,
                            Colors.YELLOW,null,null,null,null,null,null,null,null,null}),
    WP23(5, new Integer[]{  null,null,null,null,null,null,4,5,null,null,null,2,null,null,5,6,null,3,1,null},
            new Colors[]{   null,Colors.BLUE,Colors.RED,null,null,null,null,null,null,Colors.BLUE,
                            Colors.BLUE,null,null,Colors.RED,null,null,Colors.RED,null,null,null}),
    WP42(0, new Integer[]{  null,null,null,null,null,null,null,null,null,null,
                            null,null,null,null,null,null,null,null,null,null},
            new Colors[]{   null,null,null,null,null,null,null,null,null,null,
                            null,null,null,null,null,null,null,null,null,null});  //default pattern

    private final int difficulty;
    private final Integer[] cellValues;
    private final Colors[] cellColors;

    PatternValues(int difficulty, Integer[] cellValues, Colors[] cellColors){
        this.difficulty = difficulty;
        this.cellValues = cellValues;
        this.cellColors = cellColors;
    }

    int getDifficulty(){
        return difficulty;
    }

    Integer[] getCellValues (){
        return cellValues;
    }

    Colors[] getCellColors (){
        return cellColors;
    }
}
