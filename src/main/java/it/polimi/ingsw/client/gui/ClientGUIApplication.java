package it.polimi.ingsw.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.*;
import it.polimi.ingsw.shared.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

import static it.polimi.ingsw.shared.util.InterfaceMessages.*;
import static org.fusesource.jansi.Ansi.ansi;


public class ClientGUIApplication extends Application implements Observer {

    private static int WINDOW_WIDTH = 700;
    private static int WINDOW_HEIGHT = 500;

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
    private Label wrTimerLabel = new Label();

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
        showLogin(primaryStage);
    }

    private void showLogin(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(WINDOW_TITLE);

        Label hostLabel = new Label(HOST_PROMPT);
        Label portLabel = new Label(PORT_PROMPT);
        Label nicknameLabel = new Label(NICKNAME_PROMPT);
        Button loginButton = new Button("Login");
        ImageView sagradaLogoImage;

        hostTextField.setPromptText(HOST_PLACEHOLDER);
        portTextField.setPromptText(PORT_PLACEHOLDER);
        nicknameTextField.setPromptText(NICKNAME_PLACEHOLDER);
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
                        waitingRoom();
                    } else {
                        loginErrorLabel.setText(InterfaceMessages.LOGIN_FAILED_USED);
                    }
                } catch (IOException e) {
                    loginErrorLabel.setText(CONNECTION_FAILED);
                    Logger.printStackTraceConditionally(e);
                    System.exit(Constants.EXIT_ERROR);
                }
            } else {
                loginErrorLabel.setText(INVALID_HOST);
            }
        } else {
            loginErrorLabel.setText(MISSING_DATA);
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

    private void waitingRoom() {
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

    /*private void theOtherShit() {

        VBox waitingVertical = new VBox();          //waiting room scene
        Button dummy = new Button("to game");
        waitingVertical.getChildren().addAll(waitingPlayersLabel, wrTimerLabel, dummy);
        waitingVertical.setAlignment(Pos.CENTER);
        waitingVertical.setSpacing(30);
        Scene waitingRoomScene = new Scene(waitingVertical, 500, 250);

        BorderPane gameInterface = new BorderPane();  //game scene
        VBox leftSide = new VBox();
        HBox toolcard = new HBox();
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
        toolcard.getChildren().addAll(toolCard1, toolCard2, toolCard3);

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

        leftSide.getChildren().addAll(toolcard, favorTokens, comment, windowPattern);
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

    private Canvas createGridCellCanvas(JsonObject jsonARG){
        Canvas canvas = new Canvas(60, 60);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);  //background color
        gc.fillRect(0,0,60,60);
        gc.setFill(Color.WHITE);
        //canvas.setOnMouseClicked(event -> );
        return canvas;
    }

    private Canvas createDieCanvas(int value, Color color){
        Canvas canvas = new Canvas(60,60);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRoundRect(0,0, 60, 60, 10, 10);
        gc.setFill(Color.WHITE);
        if (value == 1){
            gc.fillOval(25,25,10,10);
        }else if (value == 2){
            gc.fillOval(10,10,10,10);
            gc.fillOval(40,40,10,10);
        }else if (value == 3){
            gc.fillOval(10,10,10,10);
            gc.fillOval(40,40,10,10);
            gc.fillOval(25,25,10,10);
        }else if (value == 4){
            gc.fillOval(10,10,10,10);
            gc.fillOval(40,40,10,10);
            gc.fillOval(10,40,10,10);
            gc.fillOval(40,10,10,10);
        }else if (value == 5){
            gc.fillOval(10,10,10,10);
            gc.fillOval(40,40,10,10);
            gc.fillOval(10,40,10,10);
            gc.fillOval(40,10,10,10);
            gc.fillOval(25,25,10,10);
        }else if (value == 6){
            gc.fillOval(10,10,10,10);
            gc.fillOval(40,40,10,10);
            gc.fillOval(10,40,10,10);
            gc.fillOval(40,10,10,10);
            gc.fillOval(10,25,10,10);
            gc.fillOval(40,25,10,10);
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
                    addPlayerUpdateHandle(jsonArg);
                    break;
                case UPDATE_WAITING_PLAYERS:
                    updateWaitingPlayersUpdateHandle(jsonArg);
                    break;
                case WR_TIMER_TICK:
                    wrTimerTickUpdateHandle(jsonArg);
                    break;
                case GAME_TIMER_TICK:
                    gameTimerTickUpdateHandle(jsonArg);
                    break;
                case GAME_SETUP:
                    privateObjectiveCardUpdateHandle(jsonArg);
                    selectableWindowPatternsUpdateHandle(jsonArg);
                    break;
                case WINDOW_PATTERNS:
                    windowPatternsUpdateHandle(jsonArg);
                    break;
                case TOOL_CARDS:
                    toolCardsUpdateHandle(jsonArg);
                    break;
                case PUBLIC_OBJECTIVE_CARDS:
                    publicObjectiveCardsUpdateHandle(jsonArg);
                    break;
                case DRAFT_POOL:
                    draftPoolUpdateHandle(jsonArg);
                    break;
                case TURN_MANAGEMENT:
                    turnManagementUpdateHandle(jsonArg);
                    break;
                case ROUND_TRACK:
                    String roundTrack = jsonArg.get(JsonFields.CLI_STRING).getAsString();
                    long roundTrackLength = roundTrack.chars().filter(ch -> ch == ']').count();
                    Logger.println("Tracciato del Round\n" + roundTrack + "\n");
                    break;
                case PLAYERS:
                    Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
                    break;
                case FAVOR_TOKENS:
                    favorTokensUpdateHandle(jsonArg);
                    break;
                case FINAL_SCORES:
                    finalScoresUpdateHandle(jsonArg);
                    break;
                default:
                    throw new IllegalStateException("This was not supposed to happen! " + method.toString());
            }
        }
    }

    private void addPlayerUpdateHandle(JsonObject jsonArg) {

    }

    private void updateWaitingPlayersUpdateHandle(JsonObject jsonArg) {
        Platform.runLater(() -> {
            if (!client.isPatternChosen()) {
                waitingPlayersBox.getChildren().clear();
                JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
                for (JsonElement element : playersArray) {
                    String nickname = element.getAsString();
                    Label label = new Label(nickname);
                    waitingPlayersBox.getChildren().add(label);
                }
            }
        });
    }

    private void wrTimerTickUpdateHandle(JsonObject jsonArg) {
        Platform.runLater(() -> {
            wrTimerLabel.setText(jsonArg.get(JsonFields.TICK).getAsString());
        });
    }

    private void gameTimerTickUpdateHandle(JsonObject jsonArg) {

    }

    private void privateObjectiveCardUpdateHandle(JsonObject jsonArg) {

    }

    private void selectableWindowPatternsUpdateHandle(JsonObject jsonArg) {

    }

    private void windowPatternsUpdateHandle(JsonObject jsonArg) {

    }

    private void toolCardsUpdateHandle(JsonObject jsonArg) {

    }

    private void publicObjectiveCardsUpdateHandle(JsonObject jsonArg) {

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


