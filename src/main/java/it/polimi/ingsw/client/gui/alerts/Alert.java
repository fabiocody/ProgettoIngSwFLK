package it.polimi.ingsw.client.gui.alerts;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.*;


class Alert {

    private Stage window;
    private GridPane gridPane;
    private String title;

    static final int SUGGESTED_BUTTON_WIDTH = 70;

    Alert(String title) {
        this.title = title;
        init();
    }

    Stage getWindow() {
        return window;
    }

    void init() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setAlwaysOnTop(true);
    }

    void show() {
        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.showAndWait();
    }

    GridPane getGridPane() {
        if (gridPane == null) {
            gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(15, 15, 15, 15));
        }
        return gridPane;
    }

    void display(Runnable runnable) {
        runnable.run();
        show();
    }

}
