package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.*;
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
            GridPane.setHalignment(label, HPos.valueOf(textAlignment.toString()));
            label.setAlignment(Pos.valueOf(textAlignment.toString()));
            getGridPane().add(button, 0, 1);
            GridPane.setHalignment(button, HPos.CENTER);

            getWindow().setMinWidth(200);

        });
    }

    public void present(String message) {
        present(message, TextAlignment.LEFT);
    }

}
