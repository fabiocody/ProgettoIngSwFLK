package it.polimi.ingsw.client.gui.alerts;

import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import javafx.geometry.*;


public class ConfirmAlert extends AlertWindow {

    private boolean answer;

    public ConfirmAlert(String title) {
        super(title);
    }

    public boolean present(String message) {
        present(() -> {

            Label label = new Label();
            label.setText(message);
            label.setAlignment(Pos.CENTER);

            Button yesButton = new Button("Si");
            yesButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            yesButton.setOnAction(e -> onYes());
            yesButton.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) onYes();
            });

            Button noButton = new Button("No");
            noButton.setMinWidth(SUGGESTED_BUTTON_WIDTH);
            noButton.setOnAction(e -> onNo());
            noButton.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) onNo();
            });

            getGridPane().setPadding(new Insets(25, 25, 25, 25));
            setWideVGap();
            getGridPane().add(label, 0, 0, 2, 1);
            getGridPane().add(yesButton, 0, 1);
            getGridPane().add(noButton, 1, 1);

        });
        return answer;
    }

    private void onYes() {
        answer = true;
        getWindow().close();
    }

    private void onNo() {
        answer = false;
        getWindow().close();
    }

}
