package it.polimi.ingsw.client.gui.alerts;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;


public class PromptAlert extends AlertWindow {

    private Label label;
    private TextField textField;
    private String text;
    private String targetNickname;

    private PromptAlert() {
        super();
    }

    private String present(String message, String prompt, String target) {
        targetNickname = target;
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

    private void onButtonClick() {
        text = textField.getText();
        if (text.equals(targetNickname)) {
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

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onButtonClick();
    }

    public static String presentReconnectionPrompt(String target) {
        return new PromptAlert().present("Sei stato sospeso.\nPer riunirti alla partita, reinserisci il tuo nickname", "Nickname", target);
    }

}
