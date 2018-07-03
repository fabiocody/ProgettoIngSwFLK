package it.polimi.ingsw.client.gui.alerts;

import java.util.NoSuchElementException;


public enum Options {

    YES("Si"),
    NO("No");

    private String string;

    Options(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static Options fromString(String string) {
        for (Options opt : values()) {
            if (opt.getString().equals(string))
                return opt;
        }
        throw new NoSuchElementException(string);
    }
}
