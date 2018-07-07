package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.ClientGUI;
import it.polimi.ingsw.shared.util.Colors;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;


public class SpinnerAlert extends AlertWindow {

    private Integer value;
    private Spinner<Integer> spinner;
    private Canvas dieCanvas;
    private Colors dieColor;

    public SpinnerAlert() {
        super();
    }

    public Integer present(String message, Colors dieColor, int from, int to, EventHandler<ActionEvent> cancelHandler) {
        this.dieColor = dieColor;
        present(() -> {

            Label label = getMessageLabel(message);
            Button okButton = getOkButton();
            okButton.setOnAction(e -> onButtonClick());
            okButton.setOnKeyPressed(this::onKeyPressed);
            Button cancelButton = getCancelButton();
            if (cancelHandler != null)
                cancelButton.setOnAction(cancelHandler);

            spinner = new Spinner<>();
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(from, to, from);
            spinner.setValueFactory(valueFactory);
            spinner.setOnMouseClicked(e -> onSpinnerClick());
            spinner.setOnKeyPressed(this::onKeyPressed);

            getGridPane().setPadding(new Insets(25, 25, 25, 25));
            getGridPane().add(label, 0, 0);
            dieCanvas = ClientGUI.createNumberedCell(spinner.getValue(), dieColor.getJavaFXColor(), ClientGUI.STANDARD_FACTOR);
            getGridPane().add(dieCanvas, 1, 0, 2, 1);
            GridPane.setHalignment(dieCanvas, HPos.CENTER);
            getGridPane().add(spinner, 0, 1);
            getGridPane().add(okButton, 1, 1);
            getGridPane().add(cancelButton, 2, 1);

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
        int tempValue = spinner.getValue();
        getGridPane().getChildren().remove(dieCanvas);
        dieCanvas = ClientGUI.createNumberedCell(tempValue, dieColor.getJavaFXColor(), ClientGUI.STANDARD_FACTOR);
        getGridPane().add(dieCanvas, 1, 0, 2, 1);
        GridPane.setHalignment(dieCanvas, HPos.CENTER);
    }

}
