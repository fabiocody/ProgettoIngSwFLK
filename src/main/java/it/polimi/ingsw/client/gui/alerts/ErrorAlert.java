package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.PicturesPaths;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;


public class ErrorAlert extends AlertWindow {

    public ErrorAlert() {
        super("Errore");
    }

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
