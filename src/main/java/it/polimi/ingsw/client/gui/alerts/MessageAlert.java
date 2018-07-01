package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;


public class MessageAlert extends AlertWindow {

    public MessageAlert(String title) {
        super(title);
    }

    public void present(String message, TextAlignment textAlignment) {
        present(() -> {

            Label label = getMessageLabel(message);
            label.setTextAlignment(textAlignment);
            Button button = getOkButton();

            setWideVGap();
            getGridPane().add(label, 0, 0);
            HPos hPos = HPos.valueOf(textAlignment.toString());
            GridPane.setHalignment(label, hPos);
            getGridPane().add(button, 0, 1);
            GridPane.setHalignment(button, HPos.CENTER);

            getWindow().setMinWidth(200);

        });
    }

    public void present(String message) {
        present(message, TextAlignment.LEFT);
    }

}
