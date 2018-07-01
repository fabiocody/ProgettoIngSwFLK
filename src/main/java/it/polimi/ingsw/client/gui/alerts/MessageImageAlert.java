package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;


public class MessageImageAlert extends AlertWindow {

    public MessageImageAlert(String title) {
        super(title);
    }

    public void present(String message, Image image, TextAlignment textAlignment) {
        present(() -> {

            Label label = getMessageLabel(message);
            label.setTextAlignment(textAlignment);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Button button = getOkButton();

            setWideVGap();
            getGridPane().add(imageView, 0, 0);
            GridPane.setHalignment(imageView, HPos.CENTER);
            getGridPane().add(label, 0, 1);
            GridPane.setHalignment(label, HPos.CENTER);
            getGridPane().add(button, 0, 2);
            GridPane.setHalignment(button, HPos.CENTER);

        });
    }

    public void present(String message, Image image) {
        present(message, image, TextAlignment.LEFT);
    }

}
