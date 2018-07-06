package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.ClientGUIApplication;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import static it.polimi.ingsw.shared.util.InterfaceMessages.WINDOW_TITLE;


public class AlertWindow {

    private Stage window;
    private GridPane gridPane;
    private String title;

    static final int SUGGESTED_BUTTON_WIDTH = 70;

    AlertWindow(String title) {
        this.title = title;
        new Thread(() -> ClientGUIApplication.addAlertWindow(this)).start();
        init();
    }

    AlertWindow() {
        this(WINDOW_TITLE);
    }

    Stage getWindow() {
        return window;
    }

    private void init() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setAlwaysOnTop(true);
    }

    private void show() {
        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.showAndWait();
    }

    GridPane getGridPane() {
        if (gridPane == null) {
            gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            setNarrowHGap();
            setNarrowVGap();
            gridPane.setPadding(new Insets(15, 15, 15, 15));
        }
        return gridPane;
    }

    Label getMessageLabel(String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMinWidth(100);
        label.setMaxWidth(300);
        return label;
    }

    Button getOkButton() {
        Button button = new Button("OK");
        button.setMinWidth(SUGGESTED_BUTTON_WIDTH);
        button.setOnAction(e -> closeWindow());
        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) closeWindow();
        });
        return button;
    }

    Button getCancelButton() {
        Button button = new Button("Annulla");
        button.setMinWidth(SUGGESTED_BUTTON_WIDTH);
        button.setOnAction(e -> closeWindow());
        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) closeWindow();
        });
        return button;
    }

    void present(Runnable runnable) {
        runnable.run();
        show();
    }

    public void closeWindow() {
        Platform.runLater(() -> window.close());
        new Thread(() -> ClientGUIApplication.removeAlertWindow(this)).start();
    }

    void setWideVGap() {
        getGridPane().setVgap(20);
    }

    void setNarrowVGap() {
        getGridPane().setVgap(10);
    }

    void setWideHGap() {
        getGridPane().setHgap(20);
    }

    void setNarrowHGap() {
        getGridPane().setHgap(10);
    }

}
