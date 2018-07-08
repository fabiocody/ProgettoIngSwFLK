package it.polimi.ingsw.client.gui.alerts;

import java.util.NoSuchElementException;


/**
 * Represents all the possible values of the buttons displayed in <code>TwoOptionsAlert</code>
 *
 * @see TwoOptionsAlert
 */
public enum Options {

    YES("Si"),
    NO("No"),
    INCREMENT("Aumentare"),
    DECREMENT("Diminuire");

    /**
     * The string used as the Button text
     */
    private String string;

    Options(String string) {
        this.string = string;
    }

    /**
     * @return the string property of the instance
     */
    public String getString() {
        return string;
    }

    /**
     * @param string the string associated with an instance
     * @return the instance corresponding to the provided string
     * @throws NoSuchElementException when no instance having the provided string property is found
     */
    public static Options fromString(String string) {
        for (Options opt : values()) {
            if (opt.getString().equals(string))
                return opt;
        }
        throw new NoSuchElementException(string);
    }
}
