package it.polimi.ingsw.client.gui.alerts;

import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;


/**
 * Used to ask the user for some text in a pop-up window
 */
public class PromptAlert extends AlertWindow {

    private Label label;
    private TextField textField;
    private String text;
    private String target;

    /**
     * Creates an AlertWindow window with the default title
     *
     * @see AlertWindow#AlertWindow()
     */
    private PromptAlert() {
        super();
    }

    /**
     * Handles the actual displaying of the AlertWindow
     *
     * @param message the message to be displayed
     * @param prompt the prompt of the TextField
     * @param target the string to be matched to close the window
     * @return the string entered by the user
     */
    private String present(String message, String prompt, String target) {
        this.target = target;
        present(() -> {

            label = getMessageLabel(message);
            textField = new TextField();
            textField.setPromptText(prompt);
            Button button = getOkButton();
            button.setOnAction(e -> onButtonClick());
            button.setOnKeyPressed(this::onKeyPressed);
            textField.setOnAction(e -> onButtonClick());

            getGridPane().add(label, 0, 0, 2, 1);
            getGridPane().add(textField, 0, 1);
            getGridPane().add(button, 1, 1);
            GridPane.setHalignment(button, HPos.RIGHT);

        });
        return text;
    }

    /**
     * Checks if the entered text matches with the target text. If they match, the window is closed, otherwise
     * the user is asked again
     */
    private void onButtonClick() {
        text = textField.getText();
        if (text.equals(target)) {
            closeWindow();
        } else {
            FadeTransition transition = new FadeTransition(Duration.millis(100), label);
            transition.setFromValue(1.0);
            transition.setToValue(0);
            transition.setCycleCount(4);
            transition.setAutoReverse(true);
            transition.play();
            textField.requestFocus();
            textField.selectAll();
        }
    }

    /**
     * Executed when a key is pressed. If the key pressed is ENTER, <code>onButtonClick</code> is called
     *
     * @param event the KeyEvent generated from the key press
     * @see PromptAlert#onButtonClick()
     */
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onButtonClick();
    }

    /**
     * Creates and presents an AlertWindow window that asks the user for his/her nickname (used when a user is suspended)
     *
     * @param target the target nickname
     * @return the text entered by the user
     * @see PromptAlert#present(String, String, String)
     */
    public static String presentReconnectionPrompt(String target) {
        return new PromptAlert().present("Sei stato sospeso.\nPer riunirti alla partita, reinserisci il tuo nickname", "Nickname", target);
    }

}
