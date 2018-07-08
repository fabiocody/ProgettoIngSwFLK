package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;


/**
 * Used to display a message in a pop-up window
 */
public class MessageAlert extends AlertWindow {

    /**
     * @param title the title of the window
     * @see AlertWindow#AlertWindow(String)
     */
    public MessageAlert(String title) {
        super(title);
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the message to be displayed
     */
    public void present(String message) {
        present(() -> {

            Label label = getMessageLabel(message);
            label.setTextAlignment(TextAlignment.CENTER);
            Button button = getOkButton();

            setWideVGap();
            getGridPane().add(label, 0, 0);
            GridPane.setHalignment(label, HPos.CENTER);
            label.setAlignment(Pos.CENTER);
            getGridPane().add(button, 0, 1);
            GridPane.setHalignment(button, HPos.CENTER);

            getWindow().setMinWidth(200);

        });
    }

}
