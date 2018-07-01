package it.polimi.ingsw.client.gui;

import com.google.gson.*;
import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.gui.alerts.*;
import it.polimi.ingsw.shared.util.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;
import java.util.stream.*;
import static it.polimi.ingsw.shared.util.InterfaceMessages.*;


public class ClientGUIApplication extends Application implements Observer {

    private static final int LOGIN_WINDOW_WIDTH = 700;
    private static final int LOGIN_WINDOW_HEIGHT = 500;
    private static final int SELECTABLE_WP_WINDOW_WIDTH = 1000;
    private static final int SELECTABLE_WP_WINDOW_HEIGHT = 700;
    static final int CELL_SIZE = 60;
    private static final int DOT_SIZE = 10;
    private static final double RESIZE_WP = 0.6;
    private static final String FX_BACKGROUND_COLOR_DARKGREY = "-fx-background-color: dimgrey;";
    private static final int CARD_SIZE = 225;
    private static final Color VALUE_CELL_COLOR = Color.SILVER;

    private Stage primaryStage;

    private static Client client;
    private static boolean clientSet = false;
    private static boolean debug;
    private String privateObjCardName;

    private ChoiceBox<String> connectionChoiceBox;
    private TextField hostTextField = new TextField();
    private TextField portTextField = new TextField();
    private TextField nicknameTextField = new TextField();
    private Label loginErrorLabel = new Label();
    private Label consoleLabel;
    private Button nextTurnButton;

    private VBox waitingPlayersBox = new VBox();
    private Label wrTimerLabel = new Label("∞");
    private Label gameTimerLabel = new Label("∞");
    private GridPane boardPane;
    private List<ImageView> toolCards = new ArrayList<>();
    private List<ImageView> publicObjectiveCards = new ArrayList<>();
    private List<GridPane> windowPatterns = new ArrayList<>();
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
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            boolean answer = new ConfirmAlert(WINDOW_TITLE).present(EXIT_MESSAGE);
            if (answer) primaryStage.close();
        });
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

        Image sagrada = new Image(PicturesPaths.SAGRADA_LOGO);
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

        Scene loginScene = new Scene(loginPane, LOGIN_WINDOW_WIDTH, LOGIN_WINDOW_HEIGHT);
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
        waitingPlayersBox.setAlignment(Pos.CENTER_RIGHT);
        wrTimerLabel.setMinWidth(CELL_SIZE * 2);
        wrTimerLabel.setFont(new Font(40));
        pane.add(waitingPlayersBox, 0, 0);
        pane.add(wrTimerLabel, 1, 0);

        Scene scene = new Scene(pane, LOGIN_WINDOW_WIDTH, LOGIN_WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showSelectableWindowPatterns(JsonObject jsonArg) {
        GridPane selectableWPPane = new GridPane();
        selectableWPPane.setAlignment(Pos.CENTER);
        selectableWPPane.setHgap(20);
        selectableWPPane.setVgap(10);
        selectableWPPane.setPadding(new Insets(25, 25, 25, 25));
        privateObjCardName = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD).get(JsonFields.NAME).getAsString();
        Image privateObjectiveCardImage = new Image(PicturesPaths.privateObjectiveCard(privateObjCardName));
        ImageView privateObjectiveCard = new ImageView(privateObjectiveCardImage);
        privateObjectiveCard.setFitHeight(400);
        privateObjectiveCard.setPreserveRatio(true);
        selectableWPPane.add(privateObjectiveCard, 2, 0, 1, 3);
        JsonArray wpArray = jsonArg.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        for (int i = 0; i < wpArray.size(); i++) {
            GridPane windowPattern = createWindowPattern(wpArray.get(i).getAsJsonObject(), null, 1.0);
            int column = i % 2;
            int row = (i / 2) * 2;
            selectableWPPane.add(windowPattern, column, row);
            Button button = new Button("Seleziona pattern " + (i+1));
            button.setOnAction(this::chooseWindowPattern);
            selectableWPPane.add(button, column, row+1);
            GridPane.setHalignment(button, HPos.CENTER);
        }
        Scene scene = new Scene(selectableWPPane, SELECTABLE_WP_WINDOW_WIDTH, SELECTABLE_WP_WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showBoard() {
        boardPane = new GridPane();
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setHgap(20);
        boardPane.setVgap(20);
        boardPane.setPadding(new Insets(25, 25, 25, 25));
        Image privateObjectiveCardImage = new Image(PicturesPaths.privateObjectiveCard(privateObjCardName));
        ImageView privateObjectiveCard = new ImageView(privateObjectiveCardImage);
        privateObjectiveCard.setFitHeight(CARD_SIZE);
        privateObjectiveCard.setPreserveRatio(true);
        boardPane.add(privateObjectiveCard, 3, 4);

        gameTimerLabel.setFont(new Font(30));
        gameTimerLabel.setMinWidth(CELL_SIZE * 2);
        boardPane.add(gameTimerLabel, 7, 0);

        consoleLabel = new Label();
        consoleLabel.setWrapText(true);
        consoleLabel.setFont(new Font(16));
        consoleLabel.setMaxWidth(CARD_SIZE * 2 + boardPane.getHgap() * 3);
        boardPane.add(consoleLabel, 0, 5, 3, 1);

        nextTurnButton = new Button("Termina il turno");
        nextTurnButton.setOnAction(e -> nextTurn());
        boardPane.add(nextTurnButton, 7, 4);
        GridPane.setHalignment(nextTurnButton, HPos.CENTER);
        GridPane.setValignment(nextTurnButton, VPos.BOTTOM);

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
        ClientNetwork.getInstance().deleteObserver(this);
        Logger.printStackTraceConditionally(e);
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
            Scene scene = new Scene(pane, SELECTABLE_WP_WINDOW_WIDTH, SELECTABLE_WP_WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        }
    }

    private void addPlayer(String nickname) {
        client.setNickname(nickname);
        client.setUUID(ClientNetwork.getInstance().addPlayer(nickname));
        client.setLogged(client.getUUID() != null);
    }

    private void nextTurn() {
        client.setActive(false);
        ClientNetwork.getInstance().nextTurn();
    }

    private static GridPane createWindowPattern(JsonObject wpJson, String nickname, Double resize) {
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

    private static StackPane createCellStack(JsonObject jsonCell, Double resize) {
        if (resize == null) resize = 1.0;
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
     * If color is GREY, create a value cell for Window Pattern, else create a (colored) Die
     * @param value the value of the numbered cell
     * @param color the color of the numbered cell
     * @param resize the scale value of the numbered cell
     * @return the Canvas representing the numbered cell
     */
    static Canvas createNumberedCell(int value, Color color, Double resize) {
        if (resize == null) resize = 1.0;
        Canvas canvas = new Canvas(CELL_SIZE * resize, CELL_SIZE * resize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        if (color == VALUE_CELL_COLOR) {
            gc.fillRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize);
        } else {
            gc.fillRoundRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize, 10 * resize, 10 * resize);
            gc.strokeRoundRect(0, 0, CELL_SIZE * resize, CELL_SIZE * resize, 10 * resize, 10 * resize);
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
            ComboBox<JsonObject> comboBox = new ComboBox<>();
            JsonArray diceArray = roundTrackArray.get(i).getAsJsonArray();
            List<JsonObject> diceList = StreamSupport.stream(diceArray.spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toList());
            if (diceList.isEmpty()) {
                JsonObject obj = new JsonObject();
                obj.addProperty(JsonFields.CURRENT_ROUND, i+1);
                comboBox.getItems().add(obj);
            } else {
                comboBox.getItems().addAll(diceList);
            }
            comboBox.setCellFactory(param -> new RoundTrackCell());
            comboBox.setButtonCell(new RoundTrackCell());
            comboBox.getSelectionModel().selectFirst();
            roundTrack.add(comboBox, i, 0);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork && arg instanceof JsonObject) {
            JsonObject jsonArg = (JsonObject) arg;
            if (jsonArg.has(JsonFields.METHOD)) {
                Methods method = Methods.getAsMethods(jsonArg.get(JsonFields.METHOD).getAsString());
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
                Platform.runLater(() -> {
                    new ErrorAlert().present("Errore di connessione");
                    showLogin();
                });
            }
        }
    }

    private void addPlayerUpdateHandler(JsonObject jsonArg) {
        if (jsonArg.get(JsonFields.RECONNECTED).getAsBoolean()) {
            client.setPatternChosen();
            client.setSuspended(false);
            privateObjCardName = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD).get(JsonFields.NAME).getAsString();
        }
    }

    private void updateWaitingPlayersUpdateHandler(JsonObject jsonArg) {
        if (!client.isPatternChosen()) {
            waitingPlayersBox.getChildren().clear();
            JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
            for (JsonElement element : playersArray) {
                String nickname = element.getAsString();
                Label label = new Label(nickname);
                label.setFont(new Font(20));
                waitingPlayersBox.getChildren().add(label);
            }
        }
    }

    private void wrTimerTickUpdateHandler(JsonObject jsonArg) {
        wrTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
    }

    private void gameTimerTickUpdateHandler(JsonObject jsonArg) {
        gameTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
    }

    private void windowPatternsUpdateHandler(JsonObject jsonArg) {
        windowPatterns.clear();
        JsonObject wpJson = jsonArg.getAsJsonObject(JsonFields.WINDOW_PATTERNS);
        int numberOfRivals = wpJson.entrySet().size() - 1;
        Integer columnIndex = null;
        for (Map.Entry<String, JsonElement> entry : wpJson.entrySet()) {
            String nickname = entry.getKey();
            JsonObject jsonPattern = entry.getValue().getAsJsonObject();
            if (nickname.equals(client.getNickname())) {
                GridPane pattern = createWindowPattern(jsonPattern, nickname, 1.0);
                myWindowPattern = pattern;
                windowPatterns.add(pattern);
                boardPane.add(pattern, 5, 4);
            } else {
                GridPane pattern = createWindowPattern(jsonPattern, nickname, RESIZE_WP);
                windowPatterns.add(pattern);
                if (numberOfRivals == 1) {
                    boardPane.add(pattern, 5, 1);
                } else if (numberOfRivals == 2) {
                    if (columnIndex == null) {
                        boardPane.add(pattern, 4, 1);
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
                pattern.setAlignment(Pos.BOTTOM_CENTER);
            }
        }
    }

    private void toolCardsUpdateHandler(JsonObject jsonArg) {
        JsonArray array = jsonArg.getAsJsonArray(JsonFields.TOOL_CARDS);
        for (int i = 0; i < array.size(); i++) {
            JsonObject card = array.get(i).getAsJsonObject();
            if (toolCards.size() < array.size()) {
                Image image = new Image(PicturesPaths.toolCard(card.get(JsonFields.NAME).getAsString()));
                ImageView imageView = new ImageView(image);
                toolCards.add(imageView);
                imageView.setFitHeight(CARD_SIZE);
                imageView.setPreserveRatio(true);
                boardPane.add(imageView, i, 1);
                GridPane.setValignment(imageView, VPos.CENTER);
            }
            String favorTokens = "Costo: " +
                    (card.get(JsonFields.USED).getAsBoolean() ? 2 : 1) +
                    " FT";
            Label favorTokensLabel = new Label(favorTokens);
            boardPane.add(favorTokensLabel, i, 2);
            GridPane.setHalignment(favorTokensLabel, HPos.CENTER);
        }
    }

    private void publicObjectiveCardsUpdateHandler(JsonObject jsonArg) {
        if (publicObjectiveCards.size() < Constants.NUMBER_OF_PUB_OBJ_CARDS_PER_GAME) {
            JsonArray array = jsonArg.getAsJsonArray(JsonFields.PUBLIC_OBJECTIVE_CARDS);
            for (int i = 0; i < array.size(); i++) {
                JsonObject card = array.get(i).getAsJsonObject();
                Image image = new Image(PicturesPaths.publicObjectiveCard(card.get(JsonFields.NAME).getAsString()));
                ImageView imageView = new ImageView(image);
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
        JsonArray array = jsonArg.getAsJsonArray(JsonFields.DICE);
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonDie = array.get(i).getAsJsonObject();
            Colors color = Colors.valueOf(jsonDie.get(JsonFields.COLOR).getAsString());
            int value = jsonDie.get(JsonFields.VALUE).getAsInt();
            draftPool.add(createNumberedCell(value, color.getJavaFXColor(), 1.0), i, 0);
        }
        boardPane.add(draftPool, 4, 3, 3, 1);
    }

    private void turnManagementUpdateHandler(JsonObject jsonArg) {
        if (!client.isGameStarted()) client.setGameStarted();
        client.setGameOver(jsonArg.get(JsonFields.GAME_OVER).getAsBoolean());
        List<String> suspendedPlayers = StreamSupport.stream(jsonArg.getAsJsonArray(JsonFields.SUSPENDED_PLAYERS).spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
        client.setSuspendedPlayers(suspendedPlayers);
        client.setActive(jsonArg.get(JsonFields.ACTIVE_PLAYER).getAsString());
        if (!client.isActive() && !client.isSuspended() && !client.isGameOver()) {
            consoleLabel.setText("Aspetta il tuo turno.");
            nextTurnButton.setDisable(true);
        } else if (client.isActive()) {
            consoleLabel.setText("È il tuo turno");
            nextTurnButton.setDisable(false);
        }
    }

    private void roundTrackUpdateHandler(JsonObject jsonArg) {
        if (roundTrack == null) {
            roundTrack = new GridPane();
            roundTrack.setAlignment(Pos.CENTER);
            roundTrack.setGridLinesVisible(true);
            boardPane.add(roundTrack, 0, 0, 7, 1);
            GridPane.setHalignment(roundTrack, HPos.CENTER);
        }
        createRoundTrack(jsonArg.getAsJsonArray(JsonFields.DICE));
    }

    private void favorTokensUpdateHandler(JsonObject jsonArg) {
        JsonObject jsonFavorTokens = jsonArg.getAsJsonObject(JsonFields.FAVOR_TOKENS);
        for (GridPane pane : windowPatterns) {
            String nickname = ((Label) pane.getChildren().get(pane.getChildren().size() - 3)).getText();
            int favorTokens = jsonFavorTokens.get(nickname).getAsInt();
            String favorTokensString = "Favor Tokens: " + favorTokens;
            Label favorTokensLabel = new Label(favorTokensString);
            if (nickname.equals(client.getNickname()))
                boardPane.add(favorTokensLabel, GridPane.getColumnIndex(pane), 5);
            else
                boardPane.add(favorTokensLabel, GridPane.getColumnIndex(pane), 2);
            GridPane.setHalignment(favorTokensLabel, HPos.CENTER);
        }
    }

    private void finalScoresUpdateHandler(JsonObject jsonArg) {
        // TODO
    }

}


