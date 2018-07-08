package it.polimi.ingsw.client.gui.alerts;

import it.polimi.ingsw.client.gui.ClientGUI;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import static it.polimi.ingsw.shared.util.InterfaceMessages.WINDOW_TITLE;


/**
 * The base class to all the AlertWindow pop-up windows
 */
public class AlertWindow {

    private Stage window;
    private GridPane gridPane;
    private String title;

    /**
     * The default width applied to the buttons
     */
    static final int DEFAULT_BUTTON_WIDTH = 70;

    /**
     * Creates the Window with the provided title and adds it to the list of AlertWindow in ClientGUI
     *
     * @param title the title of the window
     * @see ClientGUI#addAlertWindow(AlertWindow)
     */
    AlertWindow(String title) {
        this.title = title;
        new Thread(() -> ClientGUI.addAlertWindow(this)).start();
        init();
    }

    /**
     * Creates the Window with the default title and adds it to the list of AlertWindow in ClientGUI
     *
     * @see AlertWindow#AlertWindow(String)
     * @see ClientGUI#addAlertWindow(AlertWindow)
     */
    AlertWindow() {
        this(WINDOW_TITLE);
    }

    /**
     * @return the Stage of the AlertWindow
     */
    Stage getWindow() {
        return window;
    }

    /**
     * @return the main GridPane of the AlertWindow
     */
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

    /**
     * @param message the text to be inserted in the label
     * @return a Label object formatted to contain a message
     */
    Label getMessageLabel(String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMinWidth(100);
        label.setMaxWidth(300);
        return label;
    }

    /**
     * @param text the button will display this text
     * @return a Button object that close the window when clicked and when ENTER is pressed
     */
    private Button getStandardButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(DEFAULT_BUTTON_WIDTH);
        button.setOnAction(e -> closeWindow());
        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                closeWindow();
        });
        return button;
    }

    /**
     * @return a Button object set up to be an "OK" Button
     */
    Button getOkButton() {
        return getStandardButton("OK");
    }

    /**
     * @return a Button object set up to be a "cancel" Button
     */
    Button getCancelButton() {
        return getStandardButton("Annulla");
    }

    /**
     * Sets the VGap of the GridPane to 20
     */
    void setWideVGap() {
        getGridPane().setVgap(20);
    }

    /**
     * Sets the VGap of the GridPane to 10
     */
    void setNarrowVGap() {
        getGridPane().setVgap(10);
    }

    /**
     * Sets the HGap of the GridPane to 20
     */
    void setWideHGap() {
        getGridPane().setHgap(20);
    }

    /**
     * Sets the HGap of the GridPane to 10
     */
    void setNarrowHGap() {
        getGridPane().setHgap(10);
    }

    /**
     * Initializes the AlertWindow window
     */
    private void init() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setAlwaysOnTop(true);
    }

    /**
     * Shows the AlertWindow window and waits for it to close
     */
    private void show() {
        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Handles the showing of the AlertWindow
     *
     * @param runnable the piece of code to be run before showing the window
     */
    void present(Runnable runnable) {
        runnable.run();
        show();
    }

    /**
     * Closes the window and remove the AlertWindow from the list of Alerts in ClientGUI
     *
     * @see ClientGUI#removeAlertWindow(AlertWindow)
     */
    public void closeWindow() {
        Platform.runLater(() -> window.close());
        new Thread(() -> ClientGUI.removeAlertWindow(this)).start();
    }

}
