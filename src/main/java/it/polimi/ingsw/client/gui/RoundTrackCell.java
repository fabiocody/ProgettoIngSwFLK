package it.polimi.ingsw.client.gui;

import com.google.gson.JsonObject;
import it.polimi.ingsw.shared.util.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class RoundTrackCell extends ListCell<JsonObject> {

    @Override
    public void updateItem(JsonObject item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            if (item.has(JsonFields.CURRENT_ROUND)) {
                Label label = new Label(String.valueOf(item.get(JsonFields.CURRENT_ROUND).getAsInt()));
                label.setPrefSize(ClientGUIApplication.CELL_SIZE, ClientGUIApplication.CELL_SIZE);
                label.setTextFill(Color.BLACK);
                setGraphic(label);
            } else {
                Colors color = Colors.valueOf(item.get(JsonFields.COLOR).getAsString());
                Integer value = item.get(JsonFields.VALUE).getAsInt();
                Canvas die = ClientGUIApplication.createNumberedCell(value, color.getJavaFXColor(), 1.0);
                setGraphic(die);
            }
        }
    }

}
