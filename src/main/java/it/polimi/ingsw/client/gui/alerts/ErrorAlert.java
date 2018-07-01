package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.Paths;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;


public class ErrorAlert extends Alert {

    private ImageView errorImage;

    public ErrorAlert() {
        super("Errore");
    }

    public void display(String message) {
        display(() -> {

            Label label = new Label(message);
            label.setWrapText(true);
            label.setMinWidth(100);
            label.setMaxWidth(300);

            Image image = new Image(Paths.ERROR);
            errorImage = new ImageView(image);
            errorImage.setFitWidth(50);
            errorImage.setPreserveRatio(true);

            Button button = new Button("OK");
            button.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            button.setOnAction(e -> onButtonClick());

            getGridPane().setHgap(20);
            getGridPane().add(errorImage, 0, 0);
            getGridPane().add(label, 1, 0);
            getGridPane().add(button, 1, 1);
            GridPane.setHalignment(button, HPos.RIGHT);

            getWindow().setMinWidth(200);

        });
    }

    private void onButtonClick() {
        getWindow().close();
    }

}
