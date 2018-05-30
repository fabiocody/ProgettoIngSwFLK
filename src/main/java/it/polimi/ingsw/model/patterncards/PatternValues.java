package it.polimi.ingsw.model.patterncards;

import it.polimi.ingsw.util.Colors;

/**
 * This class contains the values and colors describing each <code>WindowPattern</code>.
 * @see     Cell
 * @see     WindowPattern
 * @author  Luca dell'Oglio
 */

public enum PatternValues {

    WP0(4,  new Integer[]{  null,null,null,null,1,null,null,5,null,4,3,null,null,null,null,2,null,null,null,null},
            new Colors[]{   Colors.YELLOW,Colors.BLUE,null,null,null,Colors.GREEN,null,null,null,null,
                            null,null, Colors.RED,null,Colors.GREEN,null,null,null,Colors.BLUE,Colors.YELLOW},
            "Kaleidoscopic Dream"),
    WP1(5,  new Integer[]{  4,null,2,5,null,null,null,6,null,2,null,3,null,4,null,5,null,1,null,null},
            new Colors[]{   null,null,null,null,Colors.GREEN,null,null,null,Colors.GREEN,null,
                            null,null,Colors.GREEN,null,null,null,Colors.GREEN,null,null,null},
            "Virtus"),
    WP2(5,  new Integer[]{  5,null,null,null,2,null,null,null,null,null,null,null,6,null,null,1,null,null,null,4},
            new Colors[]{   null,Colors.GREEN,Colors.BLUE,Colors.PURPLE,null,Colors.PURPLE,null,null,null,Colors.YELLOW,
                            Colors.YELLOW,null,null,null,Colors.PURPLE,null,null,null,Colors.GREEN,null},
            "Aurorae Magnificus"),
    WP3(4,  new Integer[]{  null,null,6,null,null,null,1,5,null,2,3,null,null,null,null,null,null,4,3,null},
            new Colors[]{   Colors.YELLOW,null,null,null,null,null,null,null,null,null,
                            null,Colors.YELLOW,Colors.RED,Colors.PURPLE,null,null,null,null,null,Colors.RED},
            "Via Lux"),
    WP4(3,  new Integer[]{  null,null,2,null,null,null,4,null,null,null,null,null,5,null,null,null,3,null,null,null},
            new Colors[]{   null,Colors.BLUE,null,null,Colors.YELLOW,null,null,null,Colors.RED,null,
                            null,null,null,Colors.YELLOW,null,Colors.GREEN,null,null,null,Colors.PURPLE},
            "Sun Catcher"),
    WP5(3,  new Integer[]{  null,6,null,null,null,null,3,null,null,null,null,5,6,2,null,null,4,null,1,null},
            new Colors[]{   Colors.BLUE,null,null,null,Colors.YELLOW,null,null,Colors.BLUE,null,null,
                            null,null,null,null,null,null,null,null,null,Colors.GREEN},
            "Bellesguard"),
    WP6(5,  new Integer[]{  null,6,null,null,3,5,null,3,null,null,null,2,null,1,null,null,1,5,null,4},
            new Colors[]{   Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null,null,null,
                            null,null,Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null},
            "Firmitas"),
    WP7(6,  new Integer[]{  2,null,5,null,1,null,6,null,2,null,null,null,4,null,null,null,3,null,5,null},
            new Colors[]{   null,null,null,null,null,Colors.YELLOW,null,Colors.PURPLE,null,Colors.RED,
                            null,Colors.BLUE,null,Colors.GREEN,null,null,null,null,null,null},
            "Symphony of Light"),
    WP8(4,  new Integer[]{  null,null,null,null,null,4,null,3,null,2,null,1,null,5,null,null,null,6,null,null},
            new Colors[]{   Colors.RED,null,Colors.BLUE,null,Colors.YELLOW,null,Colors.PURPLE,null,Colors.GREEN,null,
                            null,null,null,null,null,null,null,null,null,null},
            "Aurora Sagradis"),
    WP9(5,  new Integer[]{  1,null,3,null,6,5,4,null,2,null,null,null,5,null,1,null,null,null,3,null},
            new Colors[]{   null,Colors.RED,null,null,null,null,null,Colors.RED,null,null,
                            null,null,null,Colors.RED,null,null,null,null,null,Colors.RED},
            "Industria"),
    WP10(5, new Integer[]{  6,null,null,null,5,5,null,null,null,null,null,6,null,null,null,null,null,5,4,3},
            new Colors[]{   null,Colors.PURPLE,null,null,null,null,null,Colors.PURPLE,null,null,
                            Colors.RED,null,null,Colors.PURPLE,null,Colors.YELLOW,Colors.RED,null,null,null},
            "Shadow Thief"),
    WP11(5, new Integer[]{  null,null,6,null,null,null,5,null,4,null,3,null,null,null,2,1,4,null,5,3},
            new Colors[]{   null,null,null,null,null,null,null,Colors.BLUE,null,null,
                            null,Colors.GREEN,Colors.YELLOW,Colors.PURPLE,null,null,null,Colors.RED,null,null},
            "Batllo"),
    WP12(5, new Integer[]{  1,null,3,null,null,null,2,null,null,null,6,null,null,4,null,null,5,2,null,1},
            new Colors[]{   null,null,null,Colors.BLUE,null,null,null,Colors.BLUE,null,null,
                            null,Colors.BLUE,null,null,null,Colors.BLUE,null,null,null,null},
            "Gravitas"),
    WP13(3, new Integer[]{  null,4,null,null,6,null,null,2,null,null,null,null,null,null,1,null,null,null,null,null},
            new Colors[]{   null,null,null,Colors.YELLOW,null,Colors.RED,null,null,null,null,
                            null,null,Colors.RED,Colors.PURPLE,null,Colors.BLUE,Colors.YELLOW,null,null,null},
            "Fractal Drops"),
    WP14(5, new Integer[]{  null,1,null,null,4,6,null,2,5,null,1,null,5,3,null,null,null,null,null,null},
            new Colors[]{   null,null,Colors.GREEN,Colors.PURPLE,null,null,Colors.PURPLE,null,null,Colors.GREEN,
                            null,Colors.GREEN,null,null,Colors.PURPLE,null,null,null,null,null},
            "Lux Astram"),
    WP15(4, new Integer[]{  null,null,null,null,null,2,null,5,null,1,null,null,3,null,null,1,null,6,null,4},
            new Colors[]{   null,null,Colors.GREEN,null,null,null,Colors.YELLOW,null,Colors.BLUE,null,
                            null,Colors.RED,null,Colors.PURPLE,null,null,null,null,null,null},
            "Chromatic Splendor"),
    WP16(5, new Integer[]{  3,4,1,5,null,null,6,2,null,null,null,null,null,null,null,5,null,null,null,6},
            new Colors[]{   null,null,null,null,null,null,null,null,null,Colors.YELLOW,
                            null,null,null,Colors.YELLOW,Colors.RED,null,null,Colors.YELLOW,Colors.RED,null},
            "Freelight"),
    WP17(3, new Integer[]{  null,null,null,5,null,null,4,null,null,3,6,null,null,null,null,null,null,2,null,null},
            new Colors[]{   null,null,Colors.RED,null,null,Colors.PURPLE,null,null,Colors.GREEN,null,
                            null,null,null,Colors.BLUE,null,null,Colors.YELLOW,null,null,null},
            "Luz Celestial"),
    WP18(6, new Integer[]{  6,null,null,null,1,null,5,null,null,null,4,null,2,null,null,null,6,null,3,null},
            new Colors[]{   null,Colors.BLUE,null,null,null,null,null,Colors.BLUE,null,null,
                            null,Colors.RED,null,Colors.BLUE,null,Colors.GREEN,null,Colors.YELLOW,null,Colors.PURPLE},
            "Water of Life"),
    WP19(5, new Integer[]{  null,null,null,null,5,null,null,null,4,null,null,null,3,null,6,null,2,null,1,null},
            new Colors[]{   null,null,null,Colors.RED,null,null,null,Colors.PURPLE,null,Colors.BLUE,
                            null,Colors.BLUE,null,Colors.YELLOW,null,Colors.YELLOW,null,Colors.GREEN,null,Colors.RED},
            "Ripples of Light"),
    WP20(6, new Integer[]{  null,null,1,null,null,1,null,3,null,2,null,5,4,6,null,null,null,5,null,null},
            new Colors[]{   null,null,null,null,null,null,Colors.GREEN,null,Colors.BLUE,null,
                            Colors.BLUE,null,null,null,Colors.GREEN,null,Colors.BLUE,null,Colors.GREEN,null},
            "Lux Mundi"),
    WP21(5, new Integer[]{  null,null,2,null,6,null,4,null,5,null,null,null,null,null,5,1,2,null,3,null},
            new Colors[]{   Colors.YELLOW,null,null,null,null,null,null,null,null,Colors.YELLOW,
                            null,null,null,Colors.YELLOW,null,null,null,Colors.YELLOW,null,null},
            "Comitas"),
    WP22(6, new Integer[]{  1,null,null,null,4,null,null,null,null,6,null,null,null,5,3,null,5,4,2,1},
            new Colors[]{   null,Colors.PURPLE,Colors.YELLOW,null,null,Colors.PURPLE,Colors.YELLOW,null,null,null,
                            Colors.YELLOW,null,null,null,null,null,null,null,null,null},
            "Sun's Glory"),
    WP23(5, new Integer[]{  null,null,null,null,null,null,4,5,null,null,null,2,null,null,5,6,null,3,1,null},
            new Colors[]{   null,Colors.BLUE,Colors.RED,null,null,null,null,null,null,Colors.BLUE,
                            Colors.BLUE,null,null,Colors.RED,null,null,Colors.RED,null,null,null},
            "Fulgor del Cielo"),
    WP(0, new Integer[]{  null,null,null,null,null,null,null,null,null,null,
                            null,null,null,null,null,null,null,null,null,null},
            new Colors[]{   null,null,null,null,null,null,null,null,null,null,
                            null,null,null,null,null,null,null,null,null,null},
            "Default"); //default pattern

    private final int difficulty;
    private final Integer[] cellValues;
    private final Colors[] cellColors;
    private final String name;

    /**
     * @param   difficulty the difficulty of the pattern(can be null)
     * @param   cellValues the value of each cell (can be null)
     * @param   cellColors the color of each cell (can be null)
     * @author  Luca dell'Oglio
     */

    PatternValues(int difficulty, Integer[] cellValues, Colors[] cellColors, String name){
        this.difficulty = difficulty;
        this.cellValues = cellValues;
        this.cellColors = cellColors;
        this.name = name;
    }

    /**
     * @return  the difficulty of the pattern
     * @author  Luca dell'Oglio
     */

    int getDifficulty(){
        return difficulty;
    }

    /**
     * @return  the values of the cells of the pattern
     * @author  Luca dell'Oglio
     */

    Integer[] getCellValues (){
        return cellValues;
    }

    /**
     * @return  the colors of the cells of the pattern
     * @author  Luca dell'Oglio
     */

    Colors[] getCellColors (){
        return cellColors;
    }

    /**
     * @return  the name of the pattern
     * @author  Luca dell'Oglio
     */

    String getPatternName(){ return name;}
}
