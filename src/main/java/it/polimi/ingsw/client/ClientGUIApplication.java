package it.polimi.ingsw.client;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static it.polimi.ingsw.shared.util.Constants.MAX_NICKNAME_LENGTH;

public class ClientGUIApplication extends Application implements Observer {

    private Client client;
    private Label loginComment = new Label("");
    private final TextField nicknameText = new TextField();
    private ChoiceBox<String> connectionType;
    ImageView sagradaIntro;
    private Label waitingPlayers = new Label("mark, kai, json");
    private Label remainingTime = new Label("59");

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sagrada");

        Button loginButton = new Button("login");
        Label ipLabel = new Label("IP: ");
        Label nicknameLabel = new Label("nickname: ");
        final TextField ipText = new TextField();
        ipText.setPromptText("ip qui");
        nicknameText.setPromptText("inserisci il tuo nome utente");
        connectionType = new ChoiceBox<>(FXCollections.observableArrayList("Socket", "RMI"));
        connectionType.setTooltip(new Tooltip("Select the connection type"));

        /*Class<?> clazz = this.getClass();
        InputStream input = clazz.getResourceAsStream("it/polimi/ingsw/shared/util/images/Sagrada.jpg");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);*/
        Image sagrada = new Image("images/Sagrada.jpg");
        sagradaIntro = new ImageView(sagrada);
        sagradaIntro.setFitHeight(400);
        sagradaIntro.setFitWidth(400);
        sagradaIntro.setPreserveRatio(true);


        VBox vertical = new VBox();
        HBox horizontal = new HBox();
        VBox v1 = new VBox();
        VBox v2 = new VBox();

        v1.getChildren().add(ipLabel);
        v1.getChildren().add(nicknameLabel);
        v1.setSpacing(5);

        v2.getChildren().add(ipText);
        v2.getChildren().add(nicknameText);
        v2.setSpacing(5);

        horizontal.getChildren().add(connectionType);
        horizontal.getChildren().add(v1);
        horizontal.getChildren().add(v2);
        horizontal.setAlignment(Pos.CENTER);
        horizontal.setSpacing(20);

        vertical.getChildren().addAll(horizontal, loginButton, loginComment, sagradaIntro);
        vertical.setAlignment(Pos.CENTER);
        vertical.setSpacing(30);

        Scene loginScene = new Scene(vertical, 700, 500);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        VBox waitingVertical = new VBox();          //waiting room scene
        Button dummy = new Button("to game");
        waitingVertical.getChildren().addAll(waitingPlayers, remainingTime, dummy);
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
        TextField favorTokensText = new TextField("");
        favorTokens.getChildren().addAll(favorTokensLabel, favorTokensText);

        Label commentLabel = new Label("commenti: ");
        TextField commentText = new TextField("");
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


        loginButton.setOnAction(e -> {
            if (!ipText.getText().isEmpty() && !nicknameText.getText().isEmpty() && !connectionType.getSelectionModel().isEmpty()) {
                try {
                    this.addPlayer();
                    primaryStage.setScene(waitingRoomScene);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else loginComment.setText("non hai riempito tutti i campi");
        });

        dummy.setOnAction(e -> {
                    primaryStage.setScene(gameScene);
        });

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            Boolean answer = ConfirmBox.display("confirmation window", "are you sure you want to exit?");
            if (answer) primaryStage.close();
        });

    }


    private void addPlayer() throws IOException {
            /*String nickname = nicknameText.getText();
            client.setNickname(nickname);
            client.setUUID(client.getNetwork().addPlayer(client.getNickname()));
            client.setLogged(client.getUUID() != null);
            if (client.isLogged()) loginComment.setText("Login riuscito!");
            else {
                if (nickname.equals(""))
                    loginComment.setText("Login fallito! I nickname non possono essere vuoti");
                else if (nickname.contains(" "))
                    loginComment.setText("Login fallito! I nickname non possono contenere spazi");
                else if (nickname.length() > MAX_NICKNAME_LENGTH)
                    loginComment.setText("Login fallito! I nickname non possono essere più lunghi di 20 caratteri");
            }*/
        System.out.println("the player was added");
        }

    private HBox getOtherWindowPatterns(){
        HBox windowPatterns = new HBox();

        GridPane windowPattern1 = new GridPane();
        windowPattern1.add(createGridCellCanvas(), 0 ,0);
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
        windowPattern1.add(createGridCellCanvas(), 4 ,3);
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

    private Canvas createGridCellCanvas(){
        Canvas cellCanvas = new Canvas(20, 20);
        GraphicsContext gc = cellCanvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        return cellCanvas;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}


