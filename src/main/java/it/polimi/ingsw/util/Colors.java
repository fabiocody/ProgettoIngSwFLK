package it.polimi.ingsw.util;


public enum Colors {

    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    RESET("\u001B[0m");

    private String ansiCode;

    Colors(String ansiCode) {this.ansiCode = ansiCode;}

    public String escape() {return this.ansiCode;}

}
