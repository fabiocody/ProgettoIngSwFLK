package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.PicturesPaths;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;


/**
 * Used to display an error message in a pop-up window
 */
public class ErrorAlert extends AlertWindow {

    /**
     * Creates an AlertWindow window and set the title to "Errore"
     *
     * @see AlertWindow#AlertWindow(String)
     */
    public ErrorAlert() {
        super("Errore");
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the error message to be displayed
     */
    public void present(String message) {
        present(() -> {

            Label label = getMessageLabel(message);
            Button button = getOkButton();

            Image image = new Image(PicturesPaths.ERROR);
            ImageView errorImage = new ImageView(image);
            errorImage.setFitWidth(50);
            errorImage.setPreserveRatio(true);

            setWideHGap();
            getGridPane().add(errorImage, 0, 0);
            getGridPane().add(label, 1, 0);
            getGridPane().add(button, 1, 1);
            GridPane.setHalignment(button, HPos.RIGHT);

            getWindow().setMinWidth(200);

        });
    }


}
