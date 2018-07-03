package it.polimi.ingsw.client.gui.alerts;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import static it.polimi.ingsw.shared.util.InterfaceMessages.EXIT_MESSAGE;
import static it.polimi.ingsw.shared.util.InterfaceMessages.WINDOW_TITLE;


public class TwoOptionsAlert extends AlertWindow {

    private Options answer;

    public TwoOptionsAlert(String title) {
        super(title);
    }

    public Options present(String message, Canvas dieCanvas, Options leftOption, Options rightOption) {
        present(() -> {

            Label label = getMessageLabel(message);

            Button leftButton = new Button(leftOption.getString());
            leftButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            leftButton.setOnAction(this::onButtonClick);
            leftButton.setOnKeyPressed(this::onKeyPress);

            Button rightButton = new Button(rightOption.getString());
            rightButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            rightButton.setOnAction(this::onButtonClick);
            rightButton.setOnKeyPressed(this::onKeyPress);

            getGridPane().setPadding(new Insets(25, 25, 25, 25));
            setWideVGap();
            if (dieCanvas != null) {
                getGridPane().add(label, 0, 0);
                getGridPane().add(dieCanvas, 1, 0);
                GridPane.setHalignment(dieCanvas, HPos.CENTER);
            } else {
                getGridPane().add(label, 0, 0, 2, 1);
            }
            getGridPane().add(leftButton, 0, 1);
            getGridPane().add(rightButton, 1, 1);

        });
        return answer;
    }

    private void onButtonClick(ActionEvent e) {
        Button source = (Button) e.getSource();
        String text = source.getText();
        answer = Options.fromString(text);
        getWindow().close();
    }

    private void onKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            Button source = (Button) e.getSource();
            String text = source.getText();
            answer = Options.fromString(text);
            getWindow().close();
        }
    }

    public static Options presentExitAlert() {
        return new TwoOptionsAlert(WINDOW_TITLE).present(EXIT_MESSAGE, null, Options.YES, Options.NO);
    }

}
