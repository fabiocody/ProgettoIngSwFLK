package it.polimi.ingsw.client.gui;

import com.google.gson.*;
import it.polimi.ingsw.client.*;
import it.polimi.ingsw.model.Colors;
import it.polimi.ingsw.shared.util.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;
import static it.polimi.ingsw.shared.util.InterfaceMessages.*;


public class ClientGUIApplication extends Application implements Observer {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int CELL_SIZE = 60;
    private static final int DOT_SIZE = 10;
    private static final double RESIZE_WP = 0.6;
    private static final String FX_BACKGROUND_COLOR_DARKGREY = "-fx-background-color: dimgrey;";
    private static final int CARD_SIZE = 200;

    private Stage primaryStage;

    private static Client client;
    private static boolean clientSet = false;
    private static boolean debug;

    private ChoiceBox<String> connectionChoiceBox;
    private TextField hostTextField = new TextField();
    private TextField portTextField = new TextField();
    private TextField nicknameTextField = new TextField();
    private Label loginErrorLabel = new Label();

    private VBox waitingPlayersBox = new VBox();
    private Label wrTimerLabel = new Label("∞");
    private Label gameTimerLabel = new Label("∞");
    private GridPane boardPane;
    private ImageView privateObjectiveCard;
    private List<ImageView> toolCards = new ArrayList<>();
    private List<ImageView> publicObjectiveCards = new ArrayList<>();
    private GridPane myWindowPattern;
    private GridPane roundTrack;
    private GridPane draftPool;

    private boolean boardShown = false;


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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            boolean answer = ConfirmBox.display("", EXIT_MESSAGE);
            if (answer) primaryStage.close();
        });
        this.primaryStage = primaryStage;
        showLogin();
    }

    private void showLogin() {
        primaryStage.setTitle(WINDOW_TITLE);

        Label hostLabel = new Label(HOST_PROMPT);
        Label portLabel = new Label(PORT_PROMPT);
        Label nicknameLabel = new Label(NICKNAME_PROMPT);
        Button loginButton = new Button("Login");
        ImageView sagradaLogoImage;

        hostTextField.setPromptText(HOST_PLACEHOLDER);
        portTextField.setPromptText(PORT_PLACEHOLDER);
        nicknameTextField.setPromptText(NICKNAME_PLACEHOLDER);
        for (TextField textField : Arrays.asList(hostTextField, portTextField, nicknameTextField)) {
            textField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER))
                    loginAction();
            });
        }
        connectionChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(SOCKET, RMI));
        connectionChoiceBox.getSelectionModel().selectFirst();
        loginButton.setOnAction(e -> loginAction());

        Image sagrada = new Image("images/Sagrada.jpg");
        sagradaLogoImage = new ImageView(sagrada);
        sagradaLogoImage.setFitHeight(400);
        sagradaLogoImage.setFitWidth(400);
        sagradaLogoImage.setPreserveRatio(true);

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(20);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(25, 25, 25, 25));
        loginPane.add(connectionChoiceBox, 0, 0, 1, 3);
        loginPane.add(hostLabel, 1, 0);
        loginPane.add(hostTextField, 2, 0);
        loginPane.add(portLabel, 1, 1);
        loginPane.add(portTextField, 2, 1);
        loginPane.add(nicknameLabel, 1, 2);
        loginPane.add(nicknameTextField, 2, 2);
        loginPane.add(loginButton, 2, 3);
        GridPane.setHalignment(loginButton, HPos.RIGHT);
        loginPane.add(loginErrorLabel, 0, 4, 3, 1);
        loginPane.add(sagradaLogoImage, 0, 5, 3, 1);

        Scene loginScene = new Scene(loginPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void showWaitingRoom() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));

        waitingPlayersBox.setSpacing(10);
        wrTimerLabel.setFont(new Font(30));
        pane.add(waitingPlayersBox, 0, 0);
        pane.add(wrTimerLabel, 1, 0);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showSelectableWindowPatterns(JsonObject jsonArg) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        String privateObjCardName = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD).get(JsonFields.NAME).getAsString();
        Image privateObjectiveCardImage = new Image("images/privateObj/" + privateObjCardName + ".png");
        privateObjectiveCard = new ImageView(privateObjectiveCardImage);
        privateObjectiveCard.setFitHeight(400);
        privateObjectiveCard.setPreserveRatio(true);
        pane.add(privateObjectiveCard, 2, 0, 1, 3);
        JsonArray wpArray = jsonArg.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        for (int i = 0; i < wpArray.size(); i++) {
            GridPane windowPattern = createWindowPattern(wpArray.get(i).getAsJsonObject(), null, 1.0);
            int column = i % 2;
            int row = (i / 2) * 2;
            pane.add(windowPattern, column, row);
            Button button = new Button("Seleziona pattern " + (i+1));
            button.setOnAction(this::chooseWindowPattern);
            pane.add(button, column, row+1);
            GridPane.setHalignment(button, HPos.CENTER);
        }
        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showBoard() {
        boardPane = new GridPane();
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setHgap(20);
        boardPane.setVgap(10);
        boardPane.setPadding(new Insets(25, 25, 25, 25));
        privateObjectiveCard.setFitHeight(CARD_SIZE);
        boardPane.add(privateObjectiveCard, 4, 4);
        Scene scene = new Scene(boardPane);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        boardShown = true;
    }

    private void loginAction() {
        if (!hostTextField.getText().isEmpty() && !nicknameTextField.getText().isEmpty()) {
            String host = hostTextField.getText();
            if (Client.isValidHost(host)) {
                try {
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
                    String nickname = nicknameTextField.getText();
                    this.addPlayer(nickname);
                    if (client.isLogged()) {
                        showWaitingRoom();
                    } else {
                        loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_USED);
                    }
                } catch (IOException e) {
                    loginErrorLabel.setText(CONNECTION_FAILED);
                    Logger.printStackTraceConditionally(e);
                }
            } else {
                loginErrorLabel.setText(INVALID_HOST);
            }
        } else {
            loginErrorLabel.setText(MISSING_DATA);
        }
    }

    private void chooseWindowPattern(ActionEvent event) {
        Button source = (Button) event.getSource();
        int index = Integer.parseInt(source.getText().split(" ")[2]) - 1;
        ClientNetwork.getInstance().choosePattern(index);
        client.setPatternChosen();
        if (!client.isGameStarted()) {
            BorderPane pane = new BorderPane();
            Label label = new Label(patternSelected(index + 1));
            label.setFont(new Font(20));
            pane.setCenter(label);
            Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        }
    }

    private void addPlayer(String nickname) {
        if (nickname.equals("")) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_EMPTY);
        } else if (nickname.contains(" ")) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_SPACES);
        } else if (nickname.length() > Constants.MAX_NICKNAME_LENGTH) {
            loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_LENGTH);
        } else {
            client.setNickname(nickname);
            client.setUUID(ClientNetwork.getInstance().addPlayer(nickname));
            client.setLogged(client.getUUID() != null);
        }
    }

    /*private void theOtherShit() {

        BorderPane gameInterface = new BorderPane();  //game scene
        VBox leftSide = new VBox();
        HBox toolCards = new HBox();
        HBox favorTokens = new HBox();
        HBox comment = new HBox();
        GridPane windowPattern = new GridPane();
        VBox rightSide = new VBox();
        HBox otherWindowPatterns = new HBox();
        HBox objectiveCards = new HBox();
        GridPane draftPool = new GridPane();

        ImageView toolCard1 = new ImageView();
        ImageView toolCard2 = new ImageView();
        ImageView toolCard3 = new ImageView();
        toolCards.getChildren().addAll(toolCard1, toolCard2, toolCard3);

        Label favorTokensLabel = new Label("segnalini favore: ");
        Label favorTokensNumber = new Label("");
        favorTokens.getChildren().addAll(favorTokensLabel, favorTokensNumber);

        Label commentLabel = new Label("commenti: ");
        Label commentText = new Label("");
        comment.getChildren().addAll(commentLabel, commentText);

        windowPattern.add(new StackPane(), 0 ,0);
        windowPattern.add(new StackPane(), 1 ,0);
        windowPattern.add(new StackPane(), 2 ,0);
        windowPattern.add(new StackPane(), 3 ,0);
        windowPattern.add(new StackPane(), 4 ,0);
        windowPattern.add(new StackPane(), 0 ,1);
        windowPattern.add(new StackPane(), 1 ,1);
        windowPattern.add(new StackPane(), 2 ,1);
        windowPattern.add(new StackPane(), 3 ,1);
        windowPattern.add(new StackPane(), 4 ,1);
        windowPattern.add(new StackPane(), 0 ,2);
        windowPattern.add(new StackPane(), 1 ,2);
        windowPattern.add(new StackPane(), 2 ,2);
        windowPattern.add(new StackPane(), 3 ,2);
        windowPattern.add(new StackPane(), 4 ,2);
        windowPattern.add(new StackPane(), 0 ,3);
        windowPattern.add(new StackPane(), 1 ,3);
        windowPattern.add(new StackPane(), 2 ,3);
        windowPattern.add(new StackPane(), 3 ,3);
        windowPattern.add(new StackPane(), 4 ,3);
        windowPattern.setGridLinesVisible(true);

        leftSide.getChildren().addAll(toolCards, favorTokens, comment, windowPattern);
        gameInterface.setLeft(leftSide);

        otherWindowPatterns = this.getOtherWindowPatterns();

        ImageView privateObjective = new ImageView();
        ImageView publicObjective1 = new ImageView();
        ImageView publicObjective2 = new ImageView();
        ImageView publicObjective3 = new ImageView();
        objectiveCards.getChildren().addAll(privateObjective, publicObjective1, publicObjective2, publicObjective3);

        rightSide.getChildren().addAll(objectiveCards, otherWindowPatterns);
        gameInterface.setRight(rightSide);

        Scene gameScene = new Scene(gameInterface, 1000, 1000);

/*
        loginButton.setOnAction(e -> {
            if (!hostTextField.getText().isEmpty() && !nicknameTextField.getText().isEmpty() && !connectionChoiceBox.getSelectionModel().isEmpty()) {
                try {
                    this.addPlayer();
                    primaryStage.setScene(waitingRoomScene);
                } catch (IOException e1) {
                    Logger.printStackTrace(e1);
                }
            } else loginErrorLabel.setText("Dati non validi e/o mancanti");
        });

        dummy.setOnAction(e -> {
                    primaryStage.setScene(gameScene);
        });


    }*/

    private HBox getOtherWindowPatterns(){
        HBox windowPatterns = new HBox();

        GridPane windowPattern1 = new GridPane();
        /*windowPattern1.add(createGridCellCanvas(arg), 0 ,0);
        windowPattern1.add(createGridCellCanvas(), 1 ,0);
        windowPattern1.add(createGridCellCanvas(), 2 ,0);
        windowPattern1.add(createGridCellCanvas(), 3 ,0);
        windowPattern1.add(createGridCellCanvas(), 4 ,0);
        windowPattern1.add(createGridCellCanvas(), 0 ,1);
        windowPattern1.add(createGridCellCanvas(), 1 ,1);
        windowPattern1.add(createGridCellCanvas(), 2 ,1);
        windowPattern1.add(createGridCellCanvas(), 3 ,1);
        windowPattern1.add(createGridCellCanvas(), 4 ,1);
        windowPattern1.add(createGridCellCanvas(), 0 ,2);
        windowPattern1.add(createGridCellCanvas(), 1 ,2);
        windowPattern1.add(createGridCellCanvas(), 2 ,2);
        windowPattern1.add(createGridCellCanvas(), 3 ,2);
        windowPattern1.add(createGridCellCanvas(), 4 ,2);
        windowPattern1.add(createGridCellCanvas(), 0 ,3);
        windowPattern1.add(createGridCellCanvas(), 1 ,3);
        windowPattern1.add(createGridCellCanvas(), 2 ,3);
        windowPattern1.add(createGridCellCanvas(), 3 ,3);
        windowPattern1.add(createGridCellCanvas(), 4 ,3);*/
        windowPattern1.setGridLinesVisible(true);
        windowPatterns.getChildren().add(windowPattern1);

        /*if (numberOfPlayers > 1){
            GridPane windowPattern2 = new GridPane();
            windowPattern2.add(new StackPane(), 0 ,0);
            windowPattern2.add(new StackPane(), 1 ,0);
            windowPattern2.add(new StackPane(), 2 ,0);
            windowPattern2.add(new StackPane(), 3 ,0);
            windowPattern2.add(new StackPane(), 4 ,0);
            windowPattern2.add(new StackPane(), 0 ,1);
            windowPattern2.add(new StackPane(), 1 ,1);
            windowPattern2.add(new StackPane(), 2 ,1);
            windowPattern2.add(new StackPane(), 3 ,1);
            windowPattern2.add(new StackPane(), 4 ,1);
            windowPattern2.add(new StackPane(), 0 ,2);
            windowPattern2.add(new StackPane(), 1 ,2);
            windowPattern2.add(new StackPane(), 2 ,2);
            windowPattern2.add(new StackPane(), 3 ,2);
            windowPattern2.add(new StackPane(), 4 ,2);
            windowPattern2.add(new StackPane(), 0 ,3);
            windowPattern2.add(new StackPane(), 1 ,3);
            windowPattern2.add(new StackPane(), 2 ,3);
            windowPattern2.add(new StackPane(), 3 ,3);
            windowPattern2.add(new StackPane(), 4 ,3);
            windowPattern2.setGridLinesVisible(true);
            windowPatterns.getChildren().add(windowPattern2);
        }

        if (numberOfPlayers > 2) {
            GridPane windowPattern3 = new GridPane();
            windowPattern3.add(new StackPane(), 0 ,0);
            windowPattern3.add(new StackPane(), 1 ,0);
            windowPattern3.add(new StackPane(), 2 ,0);
            windowPattern3.add(new StackPane(), 3 ,0);
            windowPattern3.add(new StackPane(), 4 ,0);
            windowPattern3.add(new StackPane(), 0 ,1);
            windowPattern3.add(new StackPane(), 1 ,1);
            windowPattern3.add(new StackPane(), 2 ,1);
            windowPattern3.add(new StackPane(), 3 ,1);
            windowPattern3.add(new StackPane(), 4 ,1);
            windowPattern3.add(new StackPane(), 0 ,2);
            windowPattern3.add(new StackPane(), 1 ,2);
            windowPattern3.add(new StackPane(), 2 ,2);
            windowPattern3.add(new StackPane(), 3 ,2);
            windowPattern3.add(new StackPane(), 4 ,2);
            windowPattern3.add(new StackPane(), 0 ,3);
            windowPattern3.add(new StackPane(), 1 ,3);
            windowPattern3.add(new StackPane(), 2 ,3);
            windowPattern3.add(new StackPane(), 3 ,3);
            windowPattern3.add(new StackPane(), 4 ,3);
            windowPattern3.setGridLinesVisible(true);
            windowPatterns.getChildren().add(windowPattern3);
        }*/
        return windowPatterns;
    }

    private GridPane createWindowPattern(JsonObject wpJson, String nickname, Double resize) {
        if (resize == null) resize = 1.0;
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
        topLabel.setStyle(FX_BACKGROUND_COLOR_DARKGREY);
        topLabel.setTextFill(Color.WHITE);
        if (nickname != null)
            topLabel.setText(nickname);
        topLabel.setPrefWidth(5 * CELL_SIZE * resize);
        wpPane.add(topLabel, 0, 0, 5, 1);
        Label nameLabel = new Label(wpJson.get(JsonFields.NAME).getAsString());
        nameLabel.setStyle(FX_BACKGROUND_COLOR_DARKGREY);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPrefWidth(4 * CELL_SIZE * resize);
        wpPane.add(nameLabel, 0, Constants.NUMBER_OF_PATTERN_ROWS + 1, Constants.NUMBER_OF_PATTERN_COLUMNS - 1, 1);
        Label difficultyLabel = new Label(wpJson.get(JsonFields.DIFFICULTY).getAsString());
        difficultyLabel.setStyle(FX_BACKGROUND_COLOR_DARKGREY);
        difficultyLabel.setTextFill(Color.WHITE);
        difficultyLabel.setPrefWidth(CELL_SIZE * resize);
        difficultyLabel.setAlignment(Pos.BASELINE_RIGHT);
        wpPane.add(difficultyLabel, Constants.NUMBER_OF_PATTERN_COLUMNS - 1, Constants.NUMBER_OF_PATTERN_ROWS + 1);
        return wpPane;
    }

    private StackPane createCellStack(JsonObject jsonCell, Double resize) {
        if (resize == null) resize = 1.0;
        StackPane pane = new StackPane();
        Canvas canvas = new Canvas(CELL_SIZE * resize, CELL_SIZE * resize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Colors color = jsonCell.get(JsonFields.COLOR).isJsonNull() ? null : Colors.valueOf(jsonCell.get(JsonFields.COLOR).getAsString());
        Integer value = jsonCell.get(JsonFields.VALUE).isJsonNull() ? null : jsonCell.get(JsonFields.VALUE).getAsInt();
        if (value == null) {
            if (color != null)
                gc.setFill(color.getJavaFXColor());
            else
                gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize);
            pane.getChildren().add(canvas);
        } else {
            pane.getChildren().add(createNumberedCell(value, Color.SILVER, resize));
        }
        return pane;
    }

    /**
     * If color is GREY, create a value cell for Window Pattern, else create a (colored) Die
     * @param value
     * @param color
     * @return
     */
    private Canvas createNumberedCell(int value, Color color, Double resize) {
        if (resize == null) resize = 1.0;
        Canvas canvas = new Canvas(CELL_SIZE * resize, CELL_SIZE * resize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        if (color == Color.GREY)
            gc.fillRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize);
        else
            gc.fillRoundRect(0,0, CELL_SIZE * resize, CELL_SIZE * resize, 10 * resize, 10 * resize);
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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork && arg instanceof JsonObject) {
            JsonObject jsonArg = (JsonObject) arg;
            Methods method = Methods.getAsMethods(jsonArg.get(JsonFields.METHOD).getAsString());
            switch (method) {
                case ADD_PLAYER:
                    Platform.runLater(() -> addPlayerUpdateHandle(jsonArg));
                    break;
                case UPDATE_WAITING_PLAYERS:
                    Platform.runLater(() -> updateWaitingPlayersUpdateHandle(jsonArg));
                    break;
                case WR_TIMER_TICK:
                    Platform.runLater(() -> wrTimerTickUpdateHandle(jsonArg));
                    break;
                case GAME_TIMER_TICK:
                    Platform.runLater(() -> gameTimerTickUpdateHandle(jsonArg));
                    break;
                case GAME_SETUP:
                    Platform.runLater(() -> showSelectableWindowPatterns(jsonArg));
                    break;
                case WINDOW_PATTERNS:
                    Platform.runLater(() -> windowPatternsUpdateHandle(jsonArg));
                    break;
                case TOOL_CARDS:
                    Platform.runLater(() -> toolCardsUpdateHandle(jsonArg));
                    break;
                case PUBLIC_OBJECTIVE_CARDS:
                    Platform.runLater(() -> publicObjectiveCardsUpdateHandle(jsonArg));
                    break;
                case DRAFT_POOL:
                    Platform.runLater(() -> draftPoolUpdateHandle(jsonArg));
                    break;
                case TURN_MANAGEMENT:
                    Platform.runLater(() -> turnManagementUpdateHandle(jsonArg));
                    break;
                case ROUND_TRACK:
                    //String roundTrack = jsonArg.get(JsonFields.CLI_STRING).getAsString();
                    //long roundTrackLength = roundTrack.chars().filter(ch -> ch == ']').count();
                    //Logger.println("Tracciato del Round\n" + roundTrack + "\n");
                    break;
                case PLAYERS:
                    if (!boardShown)
                        Platform.runLater(this::showBoard);
                    break;
                case FAVOR_TOKENS:
                    Platform.runLater(() -> favorTokensUpdateHandle(jsonArg));
                    break;
                case FINAL_SCORES:
                    Platform.runLater(() -> finalScoresUpdateHandle(jsonArg));
                    break;
                default:
                    throw new IllegalStateException("This was not supposed to happen! " + method.toString());
            }
        }
    }

    private void addPlayerUpdateHandle(JsonObject jsonArg) {

    }

    private void updateWaitingPlayersUpdateHandle(JsonObject jsonArg) {
        if (!client.isPatternChosen()) {
            waitingPlayersBox.getChildren().clear();
            JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
            for (JsonElement element : playersArray) {
                String nickname = element.getAsString();
                Label label = new Label(nickname);
                waitingPlayersBox.getChildren().add(label);
            }
        }
    }

    private void wrTimerTickUpdateHandle(JsonObject jsonArg) {
        wrTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
    }

    private void gameTimerTickUpdateHandle(JsonObject jsonArg) {
        gameTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
    }

    private void windowPatternsUpdateHandle(JsonObject jsonArg) {

    }

    private void toolCardsUpdateHandle(JsonObject jsonArg) {
        JsonArray array = jsonArg.getAsJsonArray(JsonFields.TOOL_CARDS);
        for (int i = 0; i < array.size(); i++) {
            JsonObject card = array.get(i).getAsJsonObject();
            if (toolCards.size() < array.size()) {      // TODO Doesn't handle future updates
                Image image = new Image("images/toolCards/" + card.get(JsonFields.NAME).getAsString() + ".png");
                ImageView imageView = new ImageView(image);
                toolCards.add(imageView);
                imageView.setFitHeight(CARD_SIZE);
                imageView.setPreserveRatio(true);
                boardPane.add(imageView, i, 1);
            }
        }
    }

    private void publicObjectiveCardsUpdateHandle(JsonObject jsonArg) {
        if (publicObjectiveCards.size() < Constants.NUMBER_OF_PUB_OBJ_CARDS) {
            JsonArray array = jsonArg.getAsJsonArray(JsonFields.PUBLIC_OBJECTIVE_CARDS);
            for (int i = 0; i < array.size(); i++) {
                JsonObject card = array.get(i).getAsJsonObject();
                Image image = new Image("images/publicObj/" + card.get(JsonFields.NAME).getAsString() + ".png");
                ImageView imageView = new ImageView(image);
                publicObjectiveCards.add(imageView);
                imageView.setFitHeight(CARD_SIZE);
                imageView.setPreserveRatio(true);
                boardPane.add(imageView, i, 3);
            }
        }
    }

    private void draftPoolUpdateHandle(JsonObject jsonArg) {

    }

    private void turnManagementUpdateHandle(JsonObject jsonArg) {

    }

    private void favorTokensUpdateHandle(JsonObject jsonArg) {

    }

    private void finalScoresUpdateHandle(JsonObject jsonArg) {

    }

}


