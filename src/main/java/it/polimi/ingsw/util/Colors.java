package it.polimi.ingsw.util;


public enum Colors {

    RED("\u001B[91m"),
    GREEN("\u001B[92m"),
    YELLOW("\u001B[93m"),
    BLUE("\u001B[94m"),
    PURPLE("\u001B[95m"),
    RESET("\u001B[0m");

    private String ansiCode;

    Colors(String ansiCode) {this.ansiCode = ansiCode;}

    public String escape() {return this.ansiCode;}

}
