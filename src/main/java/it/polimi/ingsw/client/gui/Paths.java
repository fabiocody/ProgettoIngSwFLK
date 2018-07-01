package it.polimi.ingsw.client.gui;


public class Paths {

    private static final String IMAGES_PATH = "images/";
    static final String SAGRADA_LOGO = IMAGES_PATH + "var/" + jpgFile("Sagrada");
    public static final String ERROR = IMAGES_PATH + "var/" + pngFile("error");

    private static String pngFile(String name) {
        return name + ".png";
    }

    private static String jpgFile(String name) {
        return name + ".jpg";
    }

    static String privateObjectiveCard(String cardName) {
        return IMAGES_PATH + "privateObj/" + pngFile(cardName);
    }

    static String publicObjectiveCard(String cardName) {
        return IMAGES_PATH + "publicObj/" + pngFile(cardName);
    }

    static String toolCard(String cardName) {
        return IMAGES_PATH + "toolCards/" + pngFile(cardName);
    }

    private Paths() {
        throw new IllegalStateException("Cannot instantiate");
    }

}
