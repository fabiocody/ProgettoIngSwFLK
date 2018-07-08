package it.polimi.ingsw.client.gui.alerts;

import javafx.event.*;
import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import static it.polimi.ingsw.shared.util.InterfaceMessages.EXIT_MESSAGE;


/**
 * Used to display a pop-up window with two options and an optional cancel button
 */
public class TwoOptionsAlert extends AlertWindow {

    private Options answer;

    /**
     * Creates an AlertWindow window with the default title
     *
     * @see AlertWindow#AlertWindow()
     */
    public TwoOptionsAlert() {
        super();
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the message to be displayed
     * @param leftOption the option to be displayed on the left
     * @param rightOption the option to be displayed on the right
     * @param cancelHandler the action to be executed when the Cancel Button is clicked (if <code>null</code>, no Cancel Button is displayed)
     * @return the chosen option
     */
    public Options present(String message, Options leftOption, Options rightOption, EventHandler<ActionEvent> cancelHandler) {
        present(() -> {

            Label label = getMessageLabel(message);

            Button leftButton = new Button(leftOption.getString());
            leftButton.setMinWidth(DEFAULT_BUTTON_WIDTH);
            leftButton.setOnAction(this::onButtonClick);
            leftButton.setOnKeyPressed(this::onKeyPressed);

            Button rightButton = new Button(rightOption.getString());
            rightButton.setMinWidth(DEFAULT_BUTTON_WIDTH);
            rightButton.setOnAction(this::onButtonClick);
            rightButton.setOnKeyPressed(this::onKeyPressed);

            Button cancelButton = getCancelButton();
            cancelButton.setOnAction(cancelHandler);

            getGridPane().setPadding(new Insets(25, 25, 25, 25));
            setWideVGap();
            setNarrowHGap();

            int colSpan = cancelHandler != null ? 5 : 2;

            getGridPane().add(label, 0, 0, colSpan, 1);
            getGridPane().add(leftButton, 0, 1);
            GridPane.setHalignment(leftButton, HPos.LEFT);
            getGridPane().add(rightButton, 1, 1);
            GridPane.setHalignment(rightButton, HPos.RIGHT);
            if (cancelHandler != null) {
                getGridPane().add(cancelButton, 4, 1);
                GridPane.setHalignment(cancelButton, HPos.RIGHT);
            }

        });
        return answer;
    }

    /**
     * Sets answer to the chosen option and closes the window
     *
     * @param source the source Button
     */
    private void choose(Button source) {
        String text = source.getText();
        answer = Options.fromString(text);
        getWindow().close();
    }

    /**
     * Sets the answer to the clicked button's associated option
     *
     * @param e the ActionEvent generated from the mouse click
     */
    private void onButtonClick(ActionEvent e) {
        Button source = (Button) e.getSource();
        choose(source);
    }

    /**
     * If the pressed key is ENTER, sets the answer to the focused button's associated option
     *
     * @param e the KeyEvent generated from the key press
     */
    private void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            Button source = (Button) e.getSource();
            choose(source);
        }
    }

    /**
     * Creates and presents an AlertWindow window that asks the user whether or not he/she wants to close the application
     *
     * @return the chosen option
     */
    public static Options presentExitAlert() {
        return new TwoOptionsAlert().present(EXIT_MESSAGE, Options.YES, Options.NO, null);
    }

}
