package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.ClientGUIApplication;
import it.polimi.ingsw.shared.util.Colors;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;


public class SpinnerAlert extends AlertWindow {

    private int value;
    private Spinner<Integer> spinner;
    private Canvas dieCanvas;
    private Colors dieColor;

    public SpinnerAlert() {
        super();
    }

    public int present(String message, Colors dieColor, int from, int to) {
        this.dieColor = dieColor;
        present(() -> {

            Label label = getMessageLabel(message);
            Button button = getOkButton();
            button.setOnAction(e -> onButtonClick());
            button.setOnKeyPressed(this::onKeyPressed);

            spinner = new Spinner<>();
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(from, to, from);
            spinner.setValueFactory(valueFactory);
            spinner.setOnMouseClicked(e -> onSpinnerClick());
            spinner.setOnKeyPressed(this::onKeyPressed);

            getGridPane().setPadding(new Insets(25, 25, 25, 25));
            getGridPane().add(label, 0, 0);
            dieCanvas = ClientGUIApplication.createNumberedCell(spinner.getValue(), dieColor.getJavaFXColor(), ClientGUIApplication.STANDARD_FACTOR);
            getGridPane().add(dieCanvas, 1, 0);
            GridPane.setHalignment(dieCanvas, HPos.CENTER);
            getGridPane().add(spinner, 0, 1);
            getGridPane().add(button, 1, 1);

            getWindow().setMinWidth(200);

        });
        return value;
    }

    private void onButtonClick() {
        value = spinner.getValue();
        closeWindow();
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onButtonClick();
    }

    private void onSpinnerClick() {
        value = spinner.getValue();
        getGridPane().getChildren().remove(dieCanvas);
        dieCanvas = ClientGUIApplication.createNumberedCell(value, dieColor.getJavaFXColor(), ClientGUIApplication.STANDARD_FACTOR);
        getGridPane().add(dieCanvas, 1, 0);
        GridPane.setHalignment(dieCanvas, HPos.CENTER);
    }

}
