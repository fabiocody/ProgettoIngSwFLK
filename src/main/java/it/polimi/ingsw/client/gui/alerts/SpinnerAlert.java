package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.ClientGUI;
import it.polimi.ingsw.shared.util.Colors;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;


/**
 * Used to display a pop-up window asking the user to choose from a range of numbers
 */
public class SpinnerAlert extends AlertWindow {

    private Integer value;
    private Spinner<Integer> spinner;
    private Canvas dieCanvas;
    private Colors dieColor;

    /**
     * Creates an AlertWindow window with the default title
     *
     * @see AlertWindow#AlertWindow()
     */
    public SpinnerAlert() {
        super();
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the message to be displayed
     * @param dieColor the color of the die to be displayed
     * @param from the lower bound of the range of numbers (inclusive)
     * @param to the higher bound of the range of numbers (inclusive)
     * @param cancelHandler the action to be executed when the Cancel Button is clicked (if <code>null</code>, no Cancel Button is displayed)
     * @return the chose value
     */
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
            if (cancelHandler != null)
                getGridPane().add(cancelButton, 2, 1);

            getWindow().setMinWidth(200);

        });
        return value;
    }

    /**
     * Saves the value entered and closes the window
     */
    private void onButtonClick() {
        value = spinner.getValue();
        closeWindow();
    }

    /**
     * If the pressed key is ENTER, calls <code>onButtonClick</code>
     *
     * @param event the KeyEvent generated from the key press
     * @see SpinnerAlert#onButtonClick()
     */
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onButtonClick();
    }

    /**
     * Read the value from the Spinner and generates a Die with such value and the provided color
     */
    private void onSpinnerClick() {
        int tempValue = spinner.getValue();
        getGridPane().getChildren().remove(dieCanvas);
        dieCanvas = ClientGUI.createNumberedCell(tempValue, dieColor.getJavaFXColor(), ClientGUI.STANDARD_FACTOR);
        getGridPane().add(dieCanvas, 1, 0, 2, 1);
        GridPane.setHalignment(dieCanvas, HPos.CENTER);
    }

}
