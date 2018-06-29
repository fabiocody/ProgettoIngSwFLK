package it.polimi.ingsw.client.gui;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

class ConfirmBox {

    static boolean answer;

    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        label.setAlignment(Pos.CENTER);
        Button yesButton = new Button("Si");
        Button noButton = new Button("No");
        yesButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        noButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //Clicking will set answer and close window
        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        TilePane tilePane = new TilePane(Orientation.HORIZONTAL);
        tilePane.setPadding(new Insets(20, 10, 20, 0));
        tilePane.setHgap(10);
        tilePane.setVgap(8);
        tilePane.setPrefColumns(2);
        tilePane.setAlignment(Pos.CENTER);

        gridPane.add(label, 0, 0);
        tilePane.getChildren().addAll(yesButton, noButton);
        gridPane.add(tilePane, 0, 1);
        GridPane.setHalignment(tilePane, HPos.CENTER);

        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.showAndWait();
        return answer;

    }



}