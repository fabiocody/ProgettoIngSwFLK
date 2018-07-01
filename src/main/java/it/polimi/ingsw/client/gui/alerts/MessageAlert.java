package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


public class MessageAlert extends AlertWindow {

    public MessageAlert(String title) {
        super(title);
    }

    public void present(String message) {
        present(() -> {

            Label label = getMessageLabel(message);
            Button button = getOkButton();

            setWideVGap();
            getGridPane().add(label, 0, 0);
            getGridPane().add(button, 0, 1);
            GridPane.setHalignment(button, HPos.CENTER);

            getWindow().setMinWidth(200);

        });
    }

}
