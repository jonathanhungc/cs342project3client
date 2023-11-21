import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends Application {
    private Client clientConnection;
    private GameInfo info;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        clientConnection = new Client(5555, data -> {
            Platform.runLater(() -> {
                info = (GameInfo) data;
                handleServerUpdate();
            });
        });

        Scene start = createStartScene(primaryStage);
        primaryStage.setScene(start);
        primaryStage.show();
    }

    private Scene createStartScene(Stage primaryStage) {
        Button startBtn = new Button("Start");
        startBtn.setOnAction(e -> {
            clientConnection.sendCategoryRequest();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> {
            clientConnection.sendExitRequest();
            primaryStage.close();
        });

        Label messageLabel = new Label("Welcome to the Game!");

        VBox box = new VBox(40, messageLabel, startBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan;");

        return new Scene(box, 700, 800);
    }

    private Scene createCategoryScene(Stage primaryStage) {
        List<Button> categoryButtons = new ArrayList<>();

        for (String category : info.getCategories()) {
            //if (!info.isSolved(category)) {
                Button categoryBtn = new Button(category);
                categoryBtn.setOnAction(e -> {
                    clientConnection.sendSelectedCategory(category);
                    primaryStage.setScene(createGameScene(primaryStage));
                });
                categoryButtons.add(categoryBtn);
            //}
        }

        Label titleLabel = new Label("Choose a Category");

        VBox box = new VBox(40);
        box.getChildren().add(titleLabel);
        box.getChildren().addAll(categoryButtons);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan;");

        return new Scene(box, 700, 800);
    }

    private Scene createGameScene(Stage primaryStage) {
        Label instructionLabel = new Label("Guess a letter of the word!");

        Label wordLabel = new Label(info.getDisplayWord());

        TextField textField = new TextField();
        textField.setPromptText("Guess letter");
        textField.setMaxWidth(200);
        textField.setPrefHeight(60);
        textField.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        textField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 1 ? change : null));

        Label attempts = new Label(info.getAttemptsLeft() + " misses left");

        Button submitBtn = new Button("Submit");

        VBox box = new VBox(30, instructionLabel, wordLabel, textField, attempts, submitBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan;");

        submitBtn.setOnAction(e -> {
            char guess = textField.getText().charAt(0);
            clientConnection.sendLetter(String.valueOf(guess));
            textField.clear();
        });

        return new Scene(box, 800, 1000);
    }

private void handleServerUpdate() {
    // Update UI based on the received GameInfo from the server
    if (info != null) {
        switch (info.getFlag()) {
            case "selectCategory":
                // Handle the server sending categories
                break;
            case "guess":
                // Handle the server updating word guess
                break;
            case "wonRound":
                // Handle the server sending a flag that user won round
                break;
            case "lostRound":
                // Handle the server sending a flag that user lost round
                break;
            case "wonGame":
                // Handle the server sending a flag that user won the game (won 3 categories)
                break;
            case "lostGame":
                // Handle the server sending a flag that user lost the game
                break;
            default:
                // Handle other cases if needed
        }
    }
}

}
