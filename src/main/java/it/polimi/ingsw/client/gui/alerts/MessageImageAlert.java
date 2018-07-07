package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;


/**
 * Used to display a message and an image one under the other in a pop-up window
 */
public class MessageImageAlert extends AlertWindow {

    /**
     * @param title the title of the window
     * @see AlertWindow#AlertWindow(String)
     */
    public MessageImageAlert(String title) {
        super(title);
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the message to be displayed
     * @param image the image to be displayed
     */
    public void present(String message, Image image) {
        present(() -> {

            Label label = getMessageLabel(message);
            label.setTextAlignment(TextAlignment.CENTER);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Button button = getOkButton();

            setWideVGap();
            getGridPane().add(imageView, 0, 0);
            GridPane.setHalignment(imageView, HPos.CENTER);
            getGridPane().add(label, 0, 1);
            label.setAlignment(Pos.CENTER);
            GridPane.setHalignment(label, HPos.CENTER);
            getGridPane().add(button, 0, 2);
            GridPane.setHalignment(button, HPos.CENTER);

        });
    }

}
