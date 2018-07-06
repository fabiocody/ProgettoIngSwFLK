package it.polimi.ingsw.client.gui.alerts;

import javafx.event.*;
import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import static it.polimi.ingsw.shared.util.InterfaceMessages.EXIT_MESSAGE;


public class TwoOptionsAlert extends AlertWindow {

    private Options answer;

    public TwoOptionsAlert() {
        super();
    }

    public Options present(String message, Options leftOption, Options rightOption, EventHandler<ActionEvent> cancelHandler) {
        present(() -> {

            Label label = getMessageLabel(message);

            Button leftButton = new Button(leftOption.getString());
            leftButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            leftButton.setOnAction(this::onButtonClick);
            leftButton.setOnKeyPressed(this::onKeyPressed);

            Button rightButton = new Button(rightOption.getString());
            rightButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
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

    private void onButtonClick(ActionEvent e) {
        Button source = (Button) e.getSource();
        String text = source.getText();
        answer = Options.fromString(text);
        getWindow().close();
    }

    private void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            Button source = (Button) e.getSource();
            String text = source.getText();
            answer = Options.fromString(text);
            getWindow().close();
        }
    }

    public static Options presentExitAlert() {
        return new TwoOptionsAlert().present(EXIT_MESSAGE, Options.YES, Options.NO, null);
    }

}
