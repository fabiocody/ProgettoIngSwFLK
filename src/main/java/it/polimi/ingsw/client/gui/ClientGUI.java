package it.polimi.ingsw.client.gui;

import com.google.gson.*;
import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.gui.alerts.*;
import it.polimi.ingsw.shared.util.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.*;
import java.util.stream.*;
import static it.polimi.ingsw.shared.util.InterfaceMessages.*;


public class ClientGUI extends Application implements Observer {

    /*
     * Constants
     */
    private static final int LOGIN_WINDOW_WIDTH = 700;
    private static final int LOGIN_WINDOW_HEIGHT = 500;
    private static final int SELECTABLE_WP_WINDOW_WIDTH = 1000;
    private static final int SELECTABLE_WP_WINDOW_HEIGHT = 700;
    private static final int CELL_SIZE = 60;
    private static final int DOT_SIZE = 10;
    private static final double RESIZE_FACTOR = 0.6;
    public static final double STANDARD_FACTOR = 0.8;
    private static final double ROUND_TRACK_DIE_RESIZE = 0.35;
    private static final int MAX_DICE_ON_ROUND_TRACK_COLUMN = 3;
    private static final String FX_BACKGROUND_COLOR_DARKGRAY = "-fx-background-color: dimgray;";
    private static final int CARD_SIZE = 225;
    private static final Color VALUE_CELL_COLOR = Color.SILVER;

    /*
     * Attributes
     */
    private static Client client;
    private static boolean clientSet = false;
    private static boolean debug;
    private static DropShadow shadow;
    private static final List<AlertWindow> alertWindows = new ArrayList<>();
    private Thread toolCardThread;
    private Stage primaryStage;

    /*
     * Graphical (Login)
     */
    private ChoiceBox<String> connectionChoiceBox;
    private TextField hostTextField = new TextField();
    private TextField portTextField = new TextField();
    private TextField nicknameTextField = new TextField();
    private Label loginErrorLabel = createLabel(16);

    /*
     * Graphical (Waiting Room)
     */
    private VBox waitingPlayersBox = new VBox();
    private Label wrTimerLabel = createLabel(40);

    /*
     * Graphical (Game)
     */
    private String privateObjectiveCardName;
    private ImageView privateObjectiveCard;
    private Image privateObjectiveCardFront;
    private Image privateObjectiveCardBack;
    private Label gameTimerLabel = createLabel(30);
    private GridPane boardPane;
    private List<ImageView> toolCards = new ArrayList<>();
    private List<ImageView> publicObjectiveCards = new ArrayList<>();
    private List<GridPane> windowPatterns = new ArrayList<>();
    private GridPane roundTrack;
    private GridPane draftPool;
    private Label consoleLabel = createLabel(18 );
    private Button nextTurnButton;
    private Button cancelButton;

    /*
     * Animations
     */
    private List<Animation> zoomingAnimations = new ArrayList<>();
    private FadeTransition gameTimerLabelAnimation;
    private ScaleTransition privateObjectiveCardAnimation;

    /*
     * Game data
     */
    private boolean boardShown = false;
    private Integer draftPoolIndex = null;
    private Integer toolCardIndex = null;

    /*
     * Tool Cards data
     */
    private List<Colors> draftPoolColors = new ArrayList<>();
    private JsonObject requiredData = null;
    private Integer requestedDraftPoolIndex = null;
    private Integer requestedRoundTrackIndex = null;
    private Integer requestedDelta = null;
    private Integer requestedNewValue = null;
    private Integer requestedFromCellX = null;
    private Integer requestedFromCellY = null;
    private Integer requestedToCellX = null;
    private Integer requestedToCellY = null;
    private boolean movement = false;
    private Boolean stop = null;


    /*
     * Getters and setters
     */

    public static void setClient(Client c) {
        if (clientSet) {
            throw new IllegalStateException("Cannot set client more than once");
        } else {
            client = c;
            clientSet = true;
        }
    }

    public static void setDebug(boolean value) {
        debug = value;
    }

    private static void setBackground(Pane pane, String imagePath) {
        Image loginBackground = new Image(imagePath);
        BackgroundImage backgroundImage = new BackgroundImage(loginBackground, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        pane.setBackground(new Background(backgroundImage));
    }

    /**
     * This method is used to set the behaviour of the cells that are part of the client's window pattern in the case
     * of a mouse clicked event
     *
     * @param myWindowPattern the GridPane containing the window pattern
     */
    private void setMyWindowPattern(GridPane myWindowPattern) {
        for (Node node : myWindowPattern.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;
                stackPane.setOnMouseClicked(this::onCellClick);
            }
        }
    }

    /**
     * This method is used to change the console label, which displays the game messages in the main board of the game
     *
     * @param text the new text that will be displayed
     */
    private void setConsoleLabel(String text) {
        consoleLabel.setText(text);
        FadeTransition transition = new FadeTransition(Duration.millis(300), consoleLabel);
        transition.setFromValue(1.0);
        transition.setToValue(0);
        transition.setCycleCount(4);
        transition.setAutoReverse(true);
        transition.play();
    }

    public static void addAlertWindow(AlertWindow alertWindow) {
        synchronized (alertWindows) {
            alertWindows.add(alertWindow);
        }
    }

    public static void removeAlertWindow(AlertWindow alertWindow) {
        synchronized (alertWindows) {
            alertWindows.remove(alertWindow);
        }
    }

    private static void clearAlertWindows() {
        synchronized (alertWindows) {
            alertWindows.forEach(AlertWindow::closeWindow);
            alertWindows.clear();
        }
    }


    /*
     * Start
     */

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            Options exit = TwoOptionsAlert.presentExitAlert();
            if (exit == Options.YES) System.exit(Constants.EXIT_STATUS);
        });
        showLogin();
    }


    /*
     * Scenes
     */

    /**
     * This method is used to set and show the login scene
     */
    private void showLogin() {
        primaryStage.setResizable(false);

        reset();

        primaryStage.setTitle(WINDOW_TITLE);
        Label hostLabel = createLabel(HOST_PROMPT);
        Label portLabel = createLabel(PORT_PROMPT);
        Label nicknameLabel = createLabel(NICKNAME_PROMPT);
        Button loginButton = new Button("Login");
        loginButton.setEffect(getShadow());
        loginButton.setOnAction(e -> loginAction());
        loginButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                loginAction();
        });

        connectionChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(SOCKET, RMI));
        connectionChoiceBox.getSelectionModel().selectFirst();
        connectionChoiceBox.setOnAction(e -> onConnectionChoiceBoxSelection());
        connectionChoiceBox.setEffect(getShadow());
        hostTextField.setPromptText(Constants.DEFAULT_HOST);
        onConnectionChoiceBoxSelection();
        nicknameTextField.setPromptText(NICKNAME_PLACEHOLDER);
        for (TextField textField : Arrays.asList(hostTextField, portTextField, nicknameTextField)) {
            textField.setEffect(getShadow());
            textField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER))
                    loginAction();
            });
        }

        Image logoImage = new Image(PicturesPaths.LOGO);
        ImageView logo = new ImageView(logoImage);
        logo.setFitHeight(400);
        logo.setFitWidth(400);
        logo.setPreserveRatio(true);
        logo.setEffect(getShadow());

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(20);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(25, 25, 25, 25));
        loginPane.add(logo, 0, 0, 3, 1);
        loginPane.add(connectionChoiceBox, 0, 3, 1, 3);
        loginPane.add(hostLabel, 1, 3);
        GridPane.setHalignment(hostLabel, HPos.RIGHT);
        loginPane.add(hostTextField, 2, 3);
        loginPane.add(portLabel, 1, 4);
        GridPane.setHalignment(portLabel, HPos.RIGHT);
        loginPane.add(portTextField, 2, 4);
        loginPane.add(nicknameLabel, 1, 5);
        GridPane.setHalignment(nicknameLabel, HPos.RIGHT);
        loginPane.add(nicknameTextField, 2, 5);
        loginPane.add(loginButton, 2, 6);
        GridPane.setHalignment(loginButton, HPos.RIGHT);
        loginErrorLabel.setPrefHeight(2 * CELL_SIZE);
        loginPane.add(loginErrorLabel, 0, 7, 3, 1);

        setBackground(loginPane, PicturesPaths.LOGIN_BACKGROUND);

        Scene loginScene = new Scene(loginPane, LOGIN_WINDOW_WIDTH, LOGIN_WINDOW_HEIGHT);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    /**
     * This method is used to set and show the waiting room scene
     */
    private void showWaitingRoom() {
        primaryStage.setResizable(false);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));

        waitingPlayersBox.setSpacing(10);
        waitingPlayersBox.setAlignment(Pos.CENTER_RIGHT);
        wrTimerLabel.setMinWidth(CELL_SIZE * 2);
        pane.add(waitingPlayersBox, 0, 0);
        pane.add(wrTimerLabel, 1, 0);

        setBackground(pane, PicturesPaths.LOGIN_BACKGROUND);

        Scene scene = new Scene(pane, LOGIN_WINDOW_WIDTH, LOGIN_WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    /**
     * This method is used to set and show the window pattern selection scene
     *
     * @param jsonArg JsonObject containing the name of the private objective
     */
    private void showSelectableWindowPatterns(JsonObject jsonArg) {
        primaryStage.setResizable(true);

        GridPane selectableWPPane = new GridPane();
        selectableWPPane.setAlignment(Pos.CENTER);
        selectableWPPane.setHgap(20);
        selectableWPPane.setVgap(10);
        selectableWPPane.setPadding(new Insets(25, 25, 25, 25));

        privateObjectiveCardName = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD).get(JsonFields.NAME).getAsString();
        privateObjectiveCardFront = new Image(PicturesPaths.privateObjectiveCard(privateObjectiveCardName));
        privateObjectiveCard = new ImageView(privateObjectiveCardFront);
        privateObjectiveCard.setEffect(getShadow());
        privateObjectiveCard.setFitHeight(400);
        privateObjectiveCard.setPreserveRatio(true);
        selectableWPPane.add(privateObjectiveCard, 2, 0, 1, 3);

        JsonArray wpArray = jsonArg.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        for (int i = 0; i < wpArray.size(); i++) {
            GridPane windowPattern = createWindowPattern(wpArray.get(i).getAsJsonObject(), null, STANDARD_FACTOR);
            int column = i % 2;
            int row = (i / 2) * 2;
            selectableWPPane.add(windowPattern, column, row);
            Button button = new Button("Seleziona pattern " + (i+1));
            button.setOnAction(this::chooseWindowPattern);
            button.setEffect(getShadow());
            selectableWPPane.add(button, column, row+1);
            GridPane.setHalignment(button, HPos.CENTER);
        }

        setBackground(selectableWPPane, PicturesPaths.BACKGROUND);

        Scene scene = new Scene(selectableWPPane, SELECTABLE_WP_WINDOW_WIDTH, SELECTABLE_WP_WINDOW_HEIGHT);
        primaryStage.setScene(scene);

        primaryStage.setResizable(false);
    }

    /**
     * This method is used to set and show the main board scene
     */
    private void showBoard() {
        primaryStage.setResizable(true);

        boardPane = new GridPane();
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setHgap(20);
        boardPane.setVgap(20);
        boardPane.setPadding(new Insets(25, 25, 25, 25));

        createPrivateObjectiveCard();

        gameTimerLabel.setMinWidth(CELL_SIZE * 2);
        boardPane.add(gameTimerLabel, 6, 0);
        GridPane.setHalignment(gameTimerLabel, HPos.CENTER);

        consoleLabel.setPrefWidth(CARD_SIZE * 3 + boardPane.getHgap() * 3);
        consoleLabel.setPrefHeight(2 * CELL_SIZE);
        consoleLabel.setAlignment(Pos.TOP_LEFT);
        boardPane.add(consoleLabel, 0, 5, 4, 1);
        GridPane.setValignment(consoleLabel, VPos.TOP);

        nextTurnButton = new Button("Termina il turno");
        nextTurnButton.setOnAction(e -> nextTurn());
        nextTurnButton.setEffect(getShadow());
        boardPane.add(nextTurnButton, 6, 4);
        GridPane.setHalignment(nextTurnButton, HPos.LEFT);
        GridPane.setValignment(nextTurnButton, VPos.BOTTOM);

        cancelButton = new Button("Annulla");
        cancelButton.setOnAction(e -> cancelAction(true, true));
        cancelButton.setDisable(true);
        cancelButton.setEffect(getShadow());
        boardPane.add(cancelButton, 6, 4);
        GridPane.setHalignment(cancelButton, HPos.LEFT);
        GridPane.setValignment(cancelButton, VPos.TOP);

        Image logoImage = new Image(PicturesPaths.LOGO);
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(250);
        logo.setPreserveRatio(true);
        logo.setEffect(getShadow());
        boardPane.add(logo, 5, 0);
        GridPane.setHalignment(logo, HPos.CENTER);
        GridPane.setValignment(logo, VPos.BOTTOM);

        setBackground(boardPane, PicturesPaths.BACKGROUND);

        Scene scene = new Scene(boardPane);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        boardShown = true;
    }


    /*
     * Actions
     */

    private void onConnectionChoiceBoxSelection() {
        String value = connectionChoiceBox.getValue();
        if (value.equals(SOCKET)) {
            portTextField.setPromptText(String.valueOf(Constants.DEFAULT_PORT));
            portTextField.setDisable(false);
        } else {
            portTextField.setPromptText(String.valueOf(Constants.DEFAULT_RMI_PORT));
            portTextField.setDisable(true);
        }
    }

    private void loginAction() {
        if (!nicknameTextField.getText().isEmpty()) {
            String host = hostTextField.getText();
            if (host.isEmpty()) host = Constants.DEFAULT_HOST;
            if (Client.isHostValid(host)) {
                try {
                    setupConnection(host);
                    String nickname = nicknameTextField.getText();
                    if (!checkNickname(nickname)) return;
                    this.addPlayer(nickname);
                    if (client.isLogged())
                        showWaitingRoom();
                    else
                        updateLoginErrorLabel(LOGIN_FAILED_USED, null);

                } catch (IOException e) {
                    updateLoginErrorLabel(CONNECTION_FAILED, e);
                }
            } else {
                updateLoginErrorLabel(INVALID_HOST, null);
            }
        } else {
            updateLoginErrorLabel(MISSING_DATA, null);
        }
    }

    /**
     * This method is invoked when a client clicks on a button in the window pattern selection scene
     * @param event MouseClickedEvent on a button
     */
    private void chooseWindowPattern(ActionEvent event) {
        Button source = (Button) event.getSource();
        int index = Integer.parseInt(source.getText().split(" ")[2]) - 1;
        ClientNetwork.getInstance().choosePattern(index);
        client.setPatternChosen(true);
        if (!client.isGameStarted()) {
            BorderPane pane = new BorderPane();
            Label label = createLabel(patternSelected(index + 1), 20);
            pane.setCenter(label);
            setBackground(pane, PicturesPaths.BACKGROUND);
            Scene scene = new Scene(pane, SELECTABLE_WP_WINDOW_WIDTH, SELECTABLE_WP_WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        }
    }

    private void cancelAction(boolean resetConsoleLabel, boolean cancelToolCard) {
        if (toolCardIndex != null) {
            if (toolCardThread != null) {
                toolCardThread.interrupt();
                toolCardThread = null;
            }
            if (cancelToolCard)
                ClientNetwork.getInstance().cancelToolCardUsage(toolCardIndex);
        }
        resetToolCardsEnvironment();
        Platform.runLater(() -> {
            cancelButton.setDisable(true);
            nextTurnButton.setDisable(false);
            if (resetConsoleLabel) setConsoleLabel(ITS_YOUR_TURN_GUI);
            restoreZoomedNodes();
        });
    }

    private void onDraftPoolClick(MouseEvent e) {
        if (client.isActive()) {
            Canvas dieCanvas = (Canvas) e.getSource();
            if (toolCardIndex == null) {
                restoreZoomedNodes();
                draftPoolIndex = GridPane.getColumnIndex(dieCanvas);
            } else {
                requestedDraftPoolIndex = GridPane.getColumnIndex(dieCanvas);
            }
            addZoomingAnimation(dieCanvas);
            cancelButton.setDisable(false);
            nextTurnButton.setDisable(true);
        }
    }

    private void onToolCardClick(MouseEvent e) {
        if (client.isActive()) {
            restoreZoomedNodes();
            ImageView selectedToolCard = (ImageView) e.getSource();
            addZoomingAnimation(selectedToolCard);
            toolCardIndex = GridPane.getColumnIndex(selectedToolCard);
            toolCardThread = new Thread(() -> {
                try {
                    useToolCard(toolCardIndex);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    Logger.debug("ToolCardThread INTERRUPTED");
                }
            });
            toolCardThread.start();
            cancelButton.setDisable(false);
            nextTurnButton.setDisable(true);
        }
    }

    private void onCellClick(MouseEvent e) {
        StackPane cell = (StackPane) e.getSource();
        int x = GridPane.getColumnIndex(cell);
        int y = GridPane.getRowIndex(cell) - 1;
        if (toolCardIndex == null && draftPoolIndex != null) {
            placeDie(draftPoolIndex, x, y);
        } else {
            if (requestedFromCellX == null && movement){
                requestedFromCellX = x;
                requestedFromCellY = y;
            } else {
                requestedToCellX = x;
                requestedToCellY = y;
            }
        }
    }

    private void onRoundTrackCellClick(MouseEvent event) {
        if (toolCardIndex != null) {
            Canvas source = (Canvas) event.getSource();
            int column = GridPane.getColumnIndex(source);
            int row = GridPane.getRowIndex(source);
            GridPane parent = (GridPane) source.getParent();
            int round = GridPane.getColumnIndex(parent);
            int index = 0;
            for (int i = 0; i < round; i++) {
                GridPane pane = (GridPane) getNode(roundTrack, i, 1);
                index += pane.getChildren().size();
            }
            requestedRoundTrackIndex = index + column * MAX_DICE_ON_ROUND_TRACK_COLUMN + row;
        }
    }


    /**
     * This method creates the pulsing animation on a specified node
     *
     * @param node the node on which the animation will be applied
     */
    private void addZoomingAnimation(Node node) {
        ScaleTransition animation = new ScaleTransition(Duration.seconds(1), node);
        animation.setFromX(1);
        animation.setFromY(1);
        animation.setToX(1.1);
        animation.setToY(1.1);
        animation.setInterpolator(Interpolator.EASE_BOTH);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setAutoReverse(true);
        zoomingAnimations.add(animation);
        animation.play();
    }

    /**
     * This method is used to stop all zooming animations, bringing all the nodes back to their original dimension
     */
    private void restoreZoomedNodes() {
        for (Animation animation : zoomingAnimations) {
            animation.stop();
            if (animation instanceof ScaleTransition) {
                ScaleTransition scaleTransition = (ScaleTransition) animation;
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
            }
            animation.setCycleCount(1);
            animation.setAutoReverse(false);
            animation.play();
        }
        zoomingAnimations.clear();
    }

    /**
     * This method applies an animation to the game timer, only for the active player, that changes depending on how much time is left
     *
     * @param tick the remaining time for the turn
     */
    private void animateGameTimerLabel(int tick) {
        if ((tick == 20 || tick == 10) && client.isActive()) {
            if (tick == 20) {
                gameTimerLabelAnimation = new FadeTransition(Duration.millis(500), gameTimerLabel);
                gameTimerLabelAnimation.setCycleCount(2 * 10);
            } else {
                gameTimerLabel.setTextFill(Color.CRIMSON);
                if (gameTimerLabelAnimation != null)
                    gameTimerLabelAnimation.stop();
                gameTimerLabelAnimation = new FadeTransition(Duration.millis(250), gameTimerLabel);
                gameTimerLabelAnimation.setCycleCount(2 * 2 * 10);
            }
            gameTimerLabelAnimation.setFromValue(1.0);
            gameTimerLabelAnimation.setToValue(0.25);
            gameTimerLabelAnimation.setAutoReverse(true);
            gameTimerLabelAnimation.play();
        }
    }

    /**
     * This method is used to stop the game timer animation
     * @param wasActive
     */
    private void restoreGameTimerLabelAnimation(boolean wasActive) {
        if (gameTimerLabelAnimation != null && (!wasActive || !client.isActive())) {
            gameTimerLabel.setTextFill(Color.WHITE);
            gameTimerLabelAnimation.stop();
            gameTimerLabelAnimation.setToValue(1);
            gameTimerLabelAnimation.setCycleCount(1);
            gameTimerLabelAnimation.setAutoReverse(false);
            gameTimerLabelAnimation.play();
            gameTimerLabelAnimation = null;
        }
    }

    /**
     * This method applies animation to the private objective card that rotates it half way, and once finished calls
     * changeImageAndKeepRotating to finish the animation
     */
    private void startRotation() {
        if (privateObjectiveCardAnimation == null) {
            privateObjectiveCardAnimation = new ScaleTransition(Duration.millis(125), privateObjectiveCard);
            privateObjectiveCardAnimation.setFromX(1);
            privateObjectiveCardAnimation.setToX(0);
            privateObjectiveCardAnimation.setOnFinished(e -> changeImageAndKeepRotating());
            privateObjectiveCardAnimation.play();
        }
    }

    /**
     * This method is used to change the image of the private objective card, depending on which way it's being rotated,
     * and finishing the rotation animation
     */
    private void changeImageAndKeepRotating() {
        if (privateObjectiveCard.getImage() == privateObjectiveCardBack) {
            privateObjectiveCard.setImage(privateObjectiveCardFront);
        } else
            privateObjectiveCard.setImage(privateObjectiveCardBack);
        privateObjectiveCardAnimation = new ScaleTransition(Duration.millis(250), privateObjectiveCard);
        privateObjectiveCardAnimation.setFromX(0);
        privateObjectiveCardAnimation.setToX(1);
        privateObjectiveCardAnimation.setOnFinished(e -> privateObjectiveCardAnimation = null);
        privateObjectiveCardAnimation.play();
    }


    /*
     * Helper methods
     */

    private void setupConnection(String host) throws IOException {
        String connection = connectionChoiceBox.getValue();
        if (connection.equals(RMI)) {
            ClientNetwork.setInstance(new RMIClient(host, Constants.DEFAULT_RMI_PORT, debug));
        } else {
            try {
                int port = Integer.parseInt(portTextField.getText());
                ClientNetwork.setInstance(new SocketClient(host, port, debug));
            } catch (NumberFormatException e) {
                ClientNetwork.setInstance(new SocketClient(host, Constants.DEFAULT_PORT, debug));
            }
        }
        ClientNetwork.getInstance().addObserver(this);
        ClientNetwork.getInstance().setup();
    }

    private boolean checkNickname(String nickname) {
        if (nickname.equals("")) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_EMPTY);
            ClientNetwork.getInstance().deleteObserver(this);
            return false;
        } else if (nickname.contains(" ")) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_SPACES);
            ClientNetwork.getInstance().deleteObserver(this);
            return false;
        } else if (nickname.length() > Constants.MAX_NICKNAME_LENGTH) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_LENGTH);
            ClientNetwork.getInstance().deleteObserver(this);
            return false;
        }
        return true;
    }

    private void updateLoginErrorLabel(String message, Throwable e) {
        loginErrorLabel.setText(message);
        if (ClientNetwork.getInstance() != null) {
            ClientNetwork.getInstance().deleteObserver(this);
            try {
                ClientNetwork.getInstance().teardown();
            } catch (IOException e1) {
                new ErrorAlert().present(e1.getMessage());
            }
        }
        Logger.printStackTraceConditionally(e);
    }

    private static Node getNode(GridPane gridPane, int column, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row)
                return node;
        }
        throw new NoSuchElementException("Nothing found at given position");
    }

    private void reset() {
        waitingPlayersBox = new VBox();
        privateObjectiveCardName = null;
        loginErrorLabel = createLabel(16);
        consoleLabel = createLabel(18);
        nextTurnButton = null;
        wrTimerLabel = createLabel("∞", 40);
        gameTimerLabel = createLabel("∞", 30);
        boardPane = null;
        toolCards = new Vector<>();
        publicObjectiveCards = new Vector<>();
        windowPatterns = new Vector<>();
        roundTrack = null;
        draftPool = null;
        boardShown = false;
    }

    private void resetToolCardsEnvironment() {
        toolCardIndex = null;
        requiredData = null;
        stop = null;
        requestedDraftPoolIndex = null;
        requestedRoundTrackIndex = null;
        requestedDelta = null;
        requestedNewValue = null;
        requestedFromCellX = null;
        requestedFromCellY = null;
        requestedToCellX = null;
        requestedToCellY = null;
        Platform.runLater(() -> {
            cancelButton.setDisable(true);
            nextTurnButton.setDisable(false);
        });
    }

    private void resetToolCardContinue(){
        stop = null;
        requestedRoundTrackIndex = null;
        requestedDelta = null;
        requestedNewValue = null;
        requestedFromCellX = null;
        requestedFromCellY = null;
        requestedToCellX = null;
        requestedToCellY = null;
        Platform.runLater(() -> {
            cancelButton.setDisable(true);
            nextTurnButton.setDisable(false);
        });
    }

    private static DropShadow getShadow() {
        if (shadow == null) {
            shadow = new DropShadow();
            shadow.setOffsetY(3);
            shadow.setColor(Color.DARKSLATEGRAY);
        }
        return shadow;
    }

    private static Label createLabel(String content, Integer fontSize) {
        Label label = new Label(content);
        if (fontSize != null) label.setFont(new Font(fontSize));
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        label.setEffect(getShadow());
        return label;
    }

    private static Label createLabel(int fontSize) {
        return createLabel("", fontSize);
    }

    private static Label createLabel(String content) {
        return createLabel(content, null);
    }


    /*
     * Movements
     */

    private void addPlayer(String nickname) {
        client.setNickname(nickname);
        client.setUUID(ClientNetwork.getInstance().addPlayer(nickname));
        client.setLogged(client.getUUID() != null);
    }

    private void nextTurn() {
        client.setActive(false);
        ClientNetwork.getInstance().nextTurn();
    }

    private void placeDie(int draftPoolIndex, int x, int y) {
        JsonObject result = ClientNetwork.getInstance().placeDie(draftPoolIndex, x, y);
        if (result.get(JsonFields.RESULT).getAsBoolean()) {
            setConsoleLabel(InterfaceMessages.SUCCESSFUL_DIE_PLACEMENT);
        } else {
            setConsoleLabel(InterfaceMessages.UNSUCCESSFUL_DIE_PLACEMENT + result.get(JsonFields.ERROR_MESSAGE).getAsString());
        }
        this.draftPoolIndex = null;
        cancelAction(false, false);
    }

    /**
     * This method is used to request what data will be needed to use the specified tool card and then use it,
     * once the player has filled all the required fields in the requiredData JsonObject
     *
     * @param toolCardIndex the index of the specified tool card
     */
    private void useToolCard(int toolCardIndex) throws InterruptedException {
        int cardIndex;
        cardIndex = toolCardIndex;
        requiredData = ClientNetwork.getInstance().requiredData(cardIndex);
        requiredData.remove(JsonFields.METHOD);
        if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.NO_FAVOR_TOKENS) || requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD)) {
            Platform.runLater(() -> {
                setConsoleLabel(InterfaceMessages.UNSUCCESSFUL_TOOL_CARD_USAGE + requiredData.get(JsonFields.DATA).getAsJsonObject().get(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD).getAsString());
                cancelAction(false, false);
            });
        } else {
            boolean valid;
            JsonObject data = this.askForToolCardData(requiredData);
            JsonObject result = ClientNetwork.getInstance().useToolCard(cardIndex, data);
            if (result.get(JsonFields.RESULT).getAsBoolean()) {
                if (!data.has(JsonFields.CONTINUE)) {
                    Platform.runLater(() -> setConsoleLabel(SUCCESSFUL_TOOL_CARD_USAGE));
                    cancelAction(false, false);
                }
                valid = true;
            } else {
                String errorMessage = result.get(JsonFields.ERROR_MESSAGE).getAsString();
                Platform.runLater(() -> setConsoleLabel(toolCardNotUsed(errorMessage)));
                cancelAction(false, false);
                valid = false;
            }
            if (requiredData != null && requiredData.getAsJsonObject(JsonFields.DATA).has(JsonFields.CONTINUE) && valid) {
                requiredData = ClientNetwork.getInstance().requiredData(cardIndex);
                requiredData.remove(JsonFields.METHOD);
                if(requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.STOP)) {
                    Platform.runLater(() -> {
                        TwoOptionsAlert continueAlert = new TwoOptionsAlert();
                        Options answer = continueAlert.present("Vuoi continuare?", Options.YES, Options.NO, null);
                        stop = answer == Options.NO;
                    });
                    while (stop == null)
                        Thread.sleep(1);
                    requiredData.getAsJsonObject(JsonFields.DATA).addProperty(JsonFields.STOP, stop);
                }
                resetToolCardContinue();
                do {
                    Platform.runLater(() -> {
                        nextTurnButton.setDisable(true);
                        cancelButton.setDisable(false);
                    });
                    data = this.askForToolCardData(requiredData);
                    result = ClientNetwork.getInstance().useToolCard(cardIndex, data);
                    valid = result.get(JsonFields.RESULT).getAsBoolean();
                } while(!valid);
                Platform.runLater(() -> setConsoleLabel(SUCCESSFUL_TOOL_CARD_USAGE));
                cancelAction(false, false);
                resetToolCardsEnvironment();
            }
        }
    }

    private JsonObject askForToolCardData(JsonObject requiredData) throws InterruptedException {
        JsonObject data = requiredData.getAsJsonObject(JsonFields.DATA);
        if (!(data.has(JsonFields.STOP) && data.get(JsonFields.STOP).getAsBoolean())) {
            if (data.has(JsonFields.DRAFT_POOL_INDEX)) {
                Platform.runLater(() -> setConsoleLabel("Seleziona un dado dalla riserva"));
                while (requestedDraftPoolIndex == null)
                    Thread.sleep(1);
                data.addProperty(JsonFields.DRAFT_POOL_INDEX, requestedDraftPoolIndex);
            }
            if (data.has(JsonFields.ROUND_TRACK_INDEX)) {
                Platform.runLater(() -> setConsoleLabel("Seleziona un dado dal roundTrack"));
                while (requestedRoundTrackIndex == null)
                    Thread.sleep(1);
                data.addProperty(JsonFields.ROUND_TRACK_INDEX, requestedRoundTrackIndex);
            }
            if (data.has(JsonFields.DELTA)) {
                Platform.runLater(() -> {
                    TwoOptionsAlert deltaAlert = new TwoOptionsAlert();
                    Options answer = deltaAlert.present("Vuoi aumentare o diminuire il valore di questo dado", Options.DECREMENT, Options.INCREMENT, e -> cancelAction(true, true));
                    requestedDelta = answer == Options.INCREMENT ? 1 : -1;
                });
                while (requestedDelta == null)
                    Thread.sleep(1);
                data.addProperty(JsonFields.DELTA, requestedDelta);
            }
            if (data.has(JsonFields.NEW_VALUE)) {
                Platform.runLater(() -> {
                    SpinnerAlert newValueAlert = new SpinnerAlert();
                    requestedNewValue = newValueAlert.present("Quale valore vuoi assegnare al dado?", draftPoolColors.get(requestedDraftPoolIndex), 1, 6, e -> cancelAction(true, true));
                });
                while (requestedNewValue == null)
                    Thread.sleep(1);
                Platform.runLater(() -> {
                    Canvas newDie = createNumberedCell(requestedNewValue, draftPoolColors.get(requestedDraftPoolIndex).getJavaFXColor(), STANDARD_FACTOR);
                    draftPool.add(newDie, requestedDraftPoolIndex, 0);
                });
                data.addProperty(JsonFields.NEW_VALUE, requestedNewValue);
            }
            if (data.has(JsonFields.FROM_CELL_X)) {
                Platform.runLater(() -> setConsoleLabel("Seleziona la cella da cui vuoi muovere il dado"));
                movement = true;
                while (requestedFromCellX == null)
                    Thread.sleep(1);
                data.addProperty(JsonFields.FROM_CELL_X, requestedFromCellX);
                data.addProperty(JsonFields.FROM_CELL_Y, requestedFromCellY);
            }
            if (data.has(JsonFields.TO_CELL_X)) {
                Platform.runLater(() -> setConsoleLabel("Seleziona la cella in cui vuoi muovere il dado"));
                while (requestedToCellX == null)
                    Thread.sleep(1);
                data.addProperty(JsonFields.TO_CELL_X, requestedToCellX);
                data.addProperty(JsonFields.TO_CELL_Y, requestedToCellY);
            }
        }
        return data;
    }


    /*
     * Graphics generators
     */

    private void createPrivateObjectiveCard() {
        privateObjectiveCardBack = new Image(PicturesPaths.privateObjectiveCard("back"));
        privateObjectiveCardFront = new Image(PicturesPaths.privateObjectiveCard(privateObjectiveCardName));
        privateObjectiveCard = new ImageView(privateObjectiveCardBack);
        privateObjectiveCard.setEffect(getShadow());
        privateObjectiveCard.setFitHeight(CARD_SIZE);
        privateObjectiveCard.setPreserveRatio(true);
        privateObjectiveCard.setOnMouseEntered(e -> startRotation());
        privateObjectiveCard.setOnMouseExited(e -> startRotation());
        boardPane.add(privateObjectiveCard, 3, 4);
    }

    private static GridPane createWindowPattern(JsonObject wpJson, String nickname, Double resize) {
        if (resize == null) resize = STANDARD_FACTOR;
        GridPane wpPane = new GridPane();
        JsonArray grid = wpJson.getAsJsonArray(JsonFields.GRID);
        for (int i = 0; i < grid.size(); i++) {
            JsonObject cell = grid.get(i).getAsJsonObject();
            int column = i % Constants.NUMBER_OF_PATTERN_COLUMNS;
            int row = i / Constants.NUMBER_OF_PATTERN_COLUMNS + 1;
            wpPane.add(createCellStack(cell, resize), column, row);
        }
        wpPane.setGridLinesVisible(true);
        Label topLabel = new Label();
        topLabel.setStyle(FX_BACKGROUND_COLOR_DARKGRAY);
        topLabel.setTextFill(Color.WHITE);
        if (nickname != null)
            topLabel.setText(nickname);
        topLabel.setPrefWidth(5 * CELL_SIZE * resize);
        wpPane.add(topLabel, 0, 0, 5, 1);
        Label nameLabel = new Label(wpJson.get(JsonFields.NAME).getAsString());
        nameLabel.setStyle(FX_BACKGROUND_COLOR_DARKGRAY);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPrefWidth(4 * CELL_SIZE * resize);
        wpPane.add(nameLabel, 0, Constants.NUMBER_OF_PATTERN_ROWS + 1, Constants.NUMBER_OF_PATTERN_COLUMNS - 1, 1);
        int difficulty = wpJson.get(JsonFields.DIFFICULTY).getAsInt();
        String difficultyString = new String(new char[difficulty]).replace('\0', '•');
        Label difficultyLabel = new Label(difficultyString);
        difficultyLabel.setStyle(FX_BACKGROUND_COLOR_DARKGRAY);
        difficultyLabel.setTextFill(Color.WHITE);
        difficultyLabel.setPrefWidth(CELL_SIZE * resize);
        difficultyLabel.setAlignment(Pos.BASELINE_RIGHT);
        wpPane.add(difficultyLabel, Constants.NUMBER_OF_PATTERN_COLUMNS - 1, Constants.NUMBER_OF_PATTERN_ROWS + 1);
        wpPane.setEffect(getShadow());
        return wpPane;
    }

    /**
     * This method creates the stack pane representing a cell of a window pattern
     *
     * @param jsonCell JsonObject containing the information of a cell
     * @param resize the scale factor of the stack pane
     * @return
     */
    private static StackPane createCellStack(JsonObject jsonCell, Double resize) {
        if (resize == null) resize = STANDARD_FACTOR;
        StackPane pane = new StackPane();
        Canvas canvas;
        Colors color = jsonCell.get(JsonFields.COLOR).isJsonNull() ? null : Colors.valueOf(jsonCell.get(JsonFields.COLOR).getAsString());
        Integer value = jsonCell.get(JsonFields.VALUE).isJsonNull() ? null : jsonCell.get(JsonFields.VALUE).getAsInt();
        if (value == null) {
            canvas = new Canvas(CELL_SIZE * resize, CELL_SIZE * resize);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            if (color != null)
                gc.setFill(color.getJavaFXColor());
            else
                gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize);
        } else {
            canvas = createNumberedCell(value, VALUE_CELL_COLOR, resize);
        }
        pane.getChildren().add(canvas);
        if (!jsonCell.get(JsonFields.DIE).isJsonNull()) {
            JsonObject jsonDie = jsonCell.getAsJsonObject(JsonFields.DIE);
            Colors dieColor = Colors.valueOf(jsonDie.get(JsonFields.COLOR).getAsString());
            int dieValue = jsonDie.get(JsonFields.VALUE).getAsInt();
            pane.getChildren().add(createNumberedCell(dieValue, dieColor.getJavaFXColor(), resize));
        }
        return pane;
    }

    /**
     * If color is GRAY, create a value cell for Window Pattern, else create a (colored) Die
     * @param value the value of the numbered cell
     * @param color the color of the numbered cell
     * @param resize the scale value of the numbered cell
     * @return the Canvas representing the numbered cell
     */
    public static Canvas createNumberedCell(int value, Color color, Double resize) {
        if (resize == null) resize = STANDARD_FACTOR;
        Canvas canvas = new Canvas(CELL_SIZE * resize, CELL_SIZE * resize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        if (color == VALUE_CELL_COLOR) {
            gc.fillRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize);
        } else {
            gc.fillRoundRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize, 10 * resize, 10 * resize);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRoundRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize, 10 * resize, 10 * resize);
            canvas.setEffect(getShadow());
        }
        gc.setFill(Color.WHITE);
        if (value == 1) {
            gc.fillOval(25 * resize,25 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        } else if (value == 2) {
            gc.fillOval(10 * resize,10 * resize,DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        } else if (value == 3) {
            gc.fillOval(10 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(25 * resize,25 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        } else if (value == 4) {
            gc.fillOval(10 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(10 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        } else if (value == 5) {
            gc.fillOval(10 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(10 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(25 * resize,25 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        } else if (value == 6) {
            gc.fillOval(10 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(10 * resize,40 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,10 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(10 * resize,25 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
            gc.fillOval(40 * resize,25 * resize, DOT_SIZE * resize, DOT_SIZE * resize);
        }
        return canvas;
    }

    private void createRoundTrack(JsonArray roundTrackArray) {
        roundTrack.getChildren().clear();
        for (int i = 0; i < Constants.NUMBER_OF_ROUNDS; i++) {
            roundTrack.setHgap(5);
            roundTrack.setVgap(1);
            Label roundLabel = createLabel(String.valueOf(i+1), 20);
            roundLabel.setTextAlignment(TextAlignment.CENTER);
            roundLabel.setMinWidth(CELL_SIZE);
            roundLabel.setTextFill(Color.WHITE);
            roundLabel.setAlignment(Pos.CENTER);
            roundTrack.add(roundLabel, i, 0);
            GridPane.setHalignment(roundLabel, HPos.CENTER);
            JsonArray diceArray = roundTrackArray.get(i).getAsJsonArray();
            List<JsonObject> diceList = StreamSupport.stream(diceArray.spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toList());
            GridPane roundGridPane = new GridPane();
            roundGridPane.setAlignment(Pos.CENTER);
            roundGridPane.setHgap(5);
            roundGridPane.setVgap(1);
            for (int j = 0; j < diceList.size(); j++) {
                JsonObject jsonDie = diceList.get(j);
                int dieValue = jsonDie.get(JsonFields.VALUE).getAsInt();
                Colors dieColor = Colors.valueOf(jsonDie.get(JsonFields.COLOR).getAsString());
                Canvas die = createNumberedCell(dieValue, dieColor.getJavaFXColor(), ROUND_TRACK_DIE_RESIZE);
                int column = j / MAX_DICE_ON_ROUND_TRACK_COLUMN;
                int row = (j % MAX_DICE_ON_ROUND_TRACK_COLUMN);
                roundGridPane.add(die, column, row);
                GridPane.setHalignment(die, HPos.CENTER);
                die.setOnMouseClicked(this::onRoundTrackCellClick);
            }
            roundTrack.add(roundGridPane, i, 1);
            GridPane.setHalignment(roundGridPane, HPos.CENTER);
        }
        roundTrack.setGridLinesVisible(true);
    }


    /*
     * Update and updateHandlers
     */

    /**
     * This method handles the updates received from the server
     *
     * @param o observable object
     * @param arg JsonObject containing the information for the update
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork && arg instanceof JsonObject) {
            JsonObject jsonArg = (JsonObject) arg;
            if (jsonArg.has(JsonFields.METHOD)) {
                Methods method = Methods.fromString(jsonArg.get(JsonFields.METHOD).getAsString());
                switch (method) {
                    case ADD_PLAYER:
                        Platform.runLater(() -> addPlayerUpdateHandler(jsonArg));
                        break;
                    case UPDATE_WAITING_PLAYERS:
                        Platform.runLater(() -> updateWaitingPlayersUpdateHandler(jsonArg));
                        break;
                    case WR_TIMER_TICK:
                        Platform.runLater(() -> wrTimerTickUpdateHandler(jsonArg));
                        break;
                    case GAME_TIMER_TICK:
                        Platform.runLater(() -> gameTimerTickUpdateHandler(jsonArg));
                        break;
                    case GAME_SETUP:
                        Platform.runLater(() -> showSelectableWindowPatterns(jsonArg));
                        break;
                    case WINDOW_PATTERNS:
                        Platform.runLater(() -> windowPatternsUpdateHandler(jsonArg));
                        break;
                    case TOOL_CARDS:
                        Platform.runLater(() -> toolCardsUpdateHandler(jsonArg));
                        break;
                    case PUBLIC_OBJECTIVE_CARDS:
                        Platform.runLater(() -> publicObjectiveCardsUpdateHandler(jsonArg));
                        break;
                    case DRAFT_POOL:
                        Platform.runLater(() -> draftPoolUpdateHandler(jsonArg));
                        break;
                    case TURN_MANAGEMENT:
                        Platform.runLater(() -> turnManagementUpdateHandler(jsonArg));
                        break;
                    case ROUND_TRACK:
                        Platform.runLater(() -> roundTrackUpdateHandler(jsonArg));
                        break;
                    case PLAYERS:
                        if (!boardShown)
                            Platform.runLater(this::showBoard);
                        break;
                    case FAVOR_TOKENS:
                        Platform.runLater(() -> favorTokensUpdateHandler(jsonArg));
                        break;
                    case FINAL_SCORES:
                        Platform.runLater(() -> finalScoresUpdateHandler(jsonArg));
                        break;
                    default:
                        throw new IllegalStateException("This was not supposed to happen! " + method.toString());
                }
            } else if (jsonArg.has(JsonFields.EXIT_ERROR)) {
                ClientNetwork.getInstance().deleteObserver(this);
                try {
                    ClientNetwork.getInstance().teardown();
                } catch (IOException e) {
                    new ErrorAlert().present(e.getMessage());
                }
                Platform.runLater(() -> {
                    new ErrorAlert().present("Errore di connessione");
                    showLogin();
                    client.reset();
                });
            }
        }
    }

    private void addPlayerUpdateHandler(JsonObject jsonArg) {
        if (jsonArg.get(JsonFields.RECONNECTED).getAsBoolean()) {
            client.setPatternChosen(true);
            client.setSuspended(false);
            privateObjectiveCardName = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD).get(JsonFields.NAME).getAsString();
            createPrivateObjectiveCard();
        }
    }

    private void updateWaitingPlayersUpdateHandler(JsonObject jsonArg) {
        if (!client.isPatternChosen()) {
            waitingPlayersBox.getChildren().clear();
            JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
            for (JsonElement element : playersArray) {
                String nickname = element.getAsString();
                Label label = createLabel(nickname, 20);
                waitingPlayersBox.getChildren().add(label);
            }
        }
    }

    private void wrTimerTickUpdateHandler(JsonObject jsonArg) {
        wrTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
    }

    private void gameTimerTickUpdateHandler(JsonObject jsonArg) {
        String tickString = jsonArg.get(JsonFields.TICK).getAsString();
        gameTimerLabel.setText(tickString);
        int tick = Integer.parseInt(tickString);
        animateGameTimerLabel(tick);
    }

    private void windowPatternsUpdateHandler(JsonObject jsonArg) {
        windowPatterns.forEach(wp -> boardPane.getChildren().remove(wp));
        windowPatterns.clear();
        JsonObject wpJson = jsonArg.getAsJsonObject(JsonFields.WINDOW_PATTERNS);
        int numberOfRivals = wpJson.entrySet().size() - 1;
        Integer columnIndex = null;
        for (Map.Entry<String, JsonElement> entry : wpJson.entrySet()) {
            String nickname = entry.getKey();
            JsonObject jsonPattern = entry.getValue().getAsJsonObject();
            GridPane pattern;
            if (nickname.equals(client.getNickname())) {
                pattern = createWindowPattern(jsonPattern, nickname, STANDARD_FACTOR);
                setMyWindowPattern(pattern);
                windowPatterns.add(pattern);
                boardPane.add(pattern, 5, 4);
            } else {
                pattern = createWindowPattern(jsonPattern, nickname, RESIZE_FACTOR);
                windowPatterns.add(pattern);
                if (numberOfRivals == 1) {
                    boardPane.add(pattern, 5, 1);
                } else if (numberOfRivals == 2) {
                    if (columnIndex == null) {
                        boardPane.add(pattern, 5, 1);
                        columnIndex = 6;
                    } else {
                        boardPane.add(pattern, columnIndex, 1);
                    }
                } else if (numberOfRivals == 3) {
                    if (columnIndex == null) {
                        boardPane.add(pattern, 4, 1);
                        columnIndex = 5;
                    } else {
                        boardPane.add(pattern, columnIndex, 1);
                        columnIndex++;
                    }
                }
            }
            pattern.setAlignment(Pos.BOTTOM_CENTER);
        }
    }

    private void toolCardsUpdateHandler(JsonObject jsonArg) {
        JsonArray array = jsonArg.getAsJsonArray(JsonFields.TOOL_CARDS);
        for (int i = 0; i < array.size(); i++) {
            JsonObject card = array.get(i).getAsJsonObject();
            if (toolCards.size() < array.size()) {
                Image image = new Image(PicturesPaths.toolCard(card.get(JsonFields.NAME).getAsString()));
                ImageView imageView = new ImageView(image);
                imageView.setEffect(getShadow());
                toolCards.add(imageView);
                imageView.setFitHeight(CARD_SIZE);
                imageView.setPreserveRatio(true);
                boardPane.add(imageView, i, 1);
                GridPane.setValignment(imageView, VPos.CENTER);
            }
            int favorTokens = card.get(JsonFields.USED).getAsBoolean() ? 2 : 1;
            String favorTokensString = new String(new char[favorTokens]).replace('\0', '•');
            favorTokensString = "Costo " + favorTokensString;
            Label favorTokensLabel;
            try {
                favorTokensLabel = (Label) getNode(boardPane, i, 2);
            } catch (NoSuchElementException e) {
                favorTokensLabel = createLabel(20);
                boardPane.add(favorTokensLabel, i, 2);
                GridPane.setHalignment(favorTokensLabel, HPos.CENTER);
            }
            favorTokensLabel.setText(favorTokensString);
        }
    }

    private void publicObjectiveCardsUpdateHandler(JsonObject jsonArg) {
        if (publicObjectiveCards.size() < Constants.NUMBER_OF_PUB_OBJ_CARDS_PER_GAME) {
            JsonArray array = jsonArg.getAsJsonArray(JsonFields.PUBLIC_OBJECTIVE_CARDS);
            for (int i = 0; i < array.size(); i++) {
                JsonObject card = array.get(i).getAsJsonObject();
                Image image = new Image(PicturesPaths.publicObjectiveCard(card.get(JsonFields.NAME).getAsString()));
                ImageView imageView = new ImageView(image);
                imageView.setEffect(getShadow());
                publicObjectiveCards.add(imageView);
                imageView.setFitHeight(CARD_SIZE);
                imageView.setPreserveRatio(true);
                boardPane.add(imageView, i, 4);
            }
        }
    }

    private void draftPoolUpdateHandler(JsonObject jsonArg) {
        if (draftPool == null) {
            draftPool = new GridPane();
            draftPool.setAlignment(Pos.CENTER);
            draftPool.setHgap(10);
        } else {
            draftPool.getChildren().clear();
        }
        draftPoolColors = new ArrayList<>();
        JsonArray array = jsonArg.getAsJsonArray(JsonFields.DICE);
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonDie = array.get(i).getAsJsonObject();
            Colors color = Colors.valueOf(jsonDie.get(JsonFields.COLOR).getAsString());
            draftPoolColors.add(color);
            int value = jsonDie.get(JsonFields.VALUE).getAsInt();
            Canvas dieCanvas = createNumberedCell(value, color.getJavaFXColor(), STANDARD_FACTOR);
            draftPool.add(dieCanvas, i, 0);
        }
        boardPane.getChildren().remove(draftPool);
        if (windowPatterns.size() == 2)
            boardPane.add(draftPool, 5, 3);
        else
            boardPane.add(draftPool, 4, 3, 3, 1);
        draftPool.setAlignment(Pos.CENTER);
    }

    private void turnManagementUpdateHandler(JsonObject jsonArg) {
        clearAlertWindows();
        client.setGameStarted();
        client.setGameOver(jsonArg.get(JsonFields.GAME_OVER).getAsBoolean());
        List<String> suspendedPlayers = StreamSupport.stream(jsonArg.getAsJsonArray(JsonFields.SUSPENDED_PLAYERS).spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
        client.setSuspendedPlayers(suspendedPlayers);
        for (GridPane windowPattern : windowPatterns) {
            String nickname = ((Label) windowPattern.getChildren().get(windowPattern.getChildren().size() - 3)).getText();
            FadeTransition transition = new FadeTransition(Duration.millis(100), windowPattern);
            if (client.getSuspendedPlayers().contains(nickname))
                transition.setToValue(0.5);
            else
                transition.setToValue(1);
            transition.setCycleCount(1);
            transition.setAutoReverse(false);
            transition.play();
        }
        boolean wasActive = client.isActive();
        client.setActive(jsonArg.get(JsonFields.ACTIVE_PLAYER).getAsString());
        draftPool.getChildren().forEach(node -> node.setOnMouseClicked(this::onDraftPoolClick));
        toolCards.forEach(toolCard -> toolCard.setOnMouseClicked(this::onToolCardClick));
        if (!client.isActive() && !client.isGameOver()) {
            setConsoleLabel(InterfaceMessages.itsHisHerTurn(client.getActiveNickname()) + "\n"+ WAIT_FOR_YOUR_TURN);
            nextTurnButton.setDisable(true);
        } else if (client.isActive()) {
            if (!wasActive)
                setConsoleLabel(ITS_YOUR_TURN_GUI);
            nextTurnButton.setDisable(false);
        }
        if (client.isSuspended() && !client.isGameOver())
            Platform.runLater(() -> {
                String text = PromptAlert.presentReconnectionPrompt(client.getNickname());
                if (!client.isGameOver() && text != null && !text.isEmpty())
                    ClientNetwork.getInstance().addPlayer(client.getNickname());
            });
        restoreGameTimerLabelAnimation(wasActive);
    }

    private void roundTrackUpdateHandler(JsonObject jsonArg) {
        if (roundTrack == null) {
            roundTrack = new GridPane();
            roundTrack.setAlignment(Pos.CENTER);
            roundTrack.setGridLinesVisible(true);
            boardPane.add(roundTrack, 0, 0, 4, 1);
            GridPane.setHalignment(roundTrack, HPos.CENTER);
        }
        createRoundTrack(jsonArg.getAsJsonArray(JsonFields.DICE));
    }

    private void favorTokensUpdateHandler(JsonObject jsonArg) {
        JsonObject jsonFavorTokens = jsonArg.getAsJsonObject(JsonFields.FAVOR_TOKENS);
        for (GridPane pane : windowPatterns) {
            String nickname = ((Label) pane.getChildren().get(pane.getChildren().size() - 3)).getText();
            int favorTokens = jsonFavorTokens.get(nickname).getAsInt();
            String favorTokensString = new String(new char[favorTokens]).replace('\0', '•');
            favorTokensString = "Segnalini favore " + favorTokensString;
            Label label;
            int row;
            if (nickname.equals(client.getNickname())) {
                row = 5;
            } else {
                row = 2;
            }
            try {
                label = (Label) getNode(boardPane, GridPane.getColumnIndex(pane), row);
            } catch (NoSuchElementException e) {
                label = createLabel(20);
                boardPane.add(label, GridPane.getColumnIndex(pane), row);
            }
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setValignment(label, VPos.TOP);
            label.setText(favorTokensString);
        }
    }

    private void finalScoresUpdateHandler(JsonObject jsonArg) {
        JsonObject scores = jsonArg.getAsJsonObject(JsonFields.FINAL_SCORES);
        StringBuilder scoresSB = new StringBuilder();
        String winner = scores.get(JsonFields.WINNER).getAsString();
        boolean isWinner = winner.equals(client.getNickname());
        scoresSB.append(isWinner ? "Hai vinto!" : "Hai perso...")
                .append(isWinner ? "" : "\n\nIl vincitore è ")
                .append(isWinner ? "" : winner)
                .append("\n\n");
        for (Map.Entry<String, JsonElement> entry : scores.entrySet()) {
            String nickname = entry.getKey();
            if (!nickname.equals(JsonFields.WINNER)) {
                int score = entry.getValue().getAsInt();
                scoresSB.append(nickname)
                        .append(": ")
                        .append(score)
                        .append('\n');
            }
        }
        Platform.runLater(() -> {
            String title = "Risultati finali";
            clearAlertWindows();
            if (isWinner)
                new MessageImageAlert(title).present(scoresSB.toString(), new Image(PicturesPaths.CUP));
            else
                new MessageAlert(title).present(scoresSB.toString());
            ClientNetwork.getInstance().deleteObserver(this);
            try {
                ClientNetwork.getInstance().teardown();
            } catch (IOException e) {
                new ErrorAlert().present(e.getMessage());
            }
            showLogin();
            client.reset();
        });
    }

}


