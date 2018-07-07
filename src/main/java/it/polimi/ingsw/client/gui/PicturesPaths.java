package it.polimi.ingsw.client.gui;


/**
 * This class contains the paths of the pictures used in the GUI
 */
public class PicturesPaths {

    private static final String IMAGES_PATH = "images/";
    private static final String VAR_PATH = IMAGES_PATH + "var/";

    static final String LOGO = VAR_PATH + pngFile("logo");
    static final String LOGIN_BACKGROUND = VAR_PATH + pngFile("loginBackground");
    static final String BACKGROUND = VAR_PATH + pngFile("blackBackground");

    public static final String ERROR = VAR_PATH + pngFile("error");
    static final String CUP = VAR_PATH + pngFile("cup");

    private static String pngFile(String name) {
        return name + ".png";
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

    private PicturesPaths() {
        throw new IllegalStateException("Cannot instantiate");
    }

}
