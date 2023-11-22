import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends Application {
    private Client clientConnection;
    VBox categoriesList;
    VBox gameScene;
    TextField userInput;
    Button sendUserInput;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        categoriesList = new VBox(20);
        gameScene = new VBox(30);

        clientConnection = new Client(5555);

        clientConnection.setCallbackCategories(data -> {
            Platform.runLater(() -> {

                Button categoryOption = new Button(data.toString());
                categoryOption.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent actionEvent) {
                        clientConnection.sendSelectedCategory(categoryOption.getText());
                    }
                });

                categoryOption.setStyle("-fx-font-family: Arial");
                categoriesList.getChildren().add(categoryOption);
            });
        });


        clientConnection.setCallbackWord(data -> {
            Platform.runLater(() -> {

                GameInfo info = (GameInfo) data;
                primaryStage.setScene(createGameScene(info, primaryStage));

            });
        });

        clientConnection.start();

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
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial");

        return new Scene(box, 700, 800);
    }



    private Scene createCategoryScene(Stage primaryStage) {
//        List<Button> categoryButtons = new ArrayList<>();
//
//        if (info != null && info.categories != null) {
//            for (String category : info.categories) {
//                Button categoryBtn = new Button(category);
//                categoryBtn.setOnAction(e -> {
//                    clientConnection.sendSelectedCategory(category);
//                    primaryStage.setScene(createGameScene(primaryStage));
//                });
//                categoryButtons.add(categoryBtn);
//            }
//        } else {
//            // Handle the case when info or info.getCategories() is null
//            System.out.println("GameInfo or categories are null. Unable to create category scene.");
//            // You might want to provide default behavior or show an error message here
//        }
//
        Label titleLabel = new Label("Choose a Category");

        VBox box = new VBox(40);
        box.getChildren().add(titleLabel);
        box.getChildren().add(categoriesList);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");

        return new Scene(box, 700, 800);
    }

    private Scene createGameScene(GameInfo info, Stage primaryStage) {
        Label instructionLabel = new Label("Guess a letter of the word!");
        instructionLabel.setStyle("-fx-font-family: Arial;");

        for (int i = 0; i < info.wordGuess.length; i++) {
            if (info.wordGuess[i] == '\0') {
                info.wordGuess[i] = '_';
            }
        }

        Label wordLabel = new Label(new String (info.wordGuess));
        wordLabel.setStyle("-fx-font-family: Arial;");

        TextField textField = new TextField();
        textField.setPromptText("Guess letter");
        textField.setMaxWidth(200);
        textField.setPrefHeight(60);
        textField.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-font-family: Arial;");
        textField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 1 ? change : null));

        Label attempts = new Label(6 - info.misses + " misses left");
        attempts.setStyle("-fx-font-family: Arial;");

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-font-family: Arial;");

        VBox box = new VBox(30, instructionLabel, wordLabel, textField, attempts, submitBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan;");

        submitBtn.setOnAction(e -> {
            char guess = textField.getText().charAt(0);
            clientConnection.sendLetter(String.valueOf(guess).toLowerCase());
            textField.clear();
        });

        return new Scene(box, 800, 1000);
    }

private void handleServerUpdate() {
    // Update UI based on the received GameInfo from the server
//    if (info != null) {
//        switch (info.flag) {
//            case "selectCategory":
//                // Handle the server sending categories
//                break;
//            case "guess":
//                // Handle the server updating word guess
//                break;
//            case "wonRound":
//                // Handle the server sending a flag that user won round
//                break;
//            case "lostRound":
//                // Handle the server sending a flag that user lost round
//                break;
//            case "wonGame":
//                // Handle the server sending a flag that user won the game (won 3 categories)
//                break;
//            case "lostGame":
//                // Handle the server sending a flag that user lost the game
//                break;
//            default:
//                // Handle other cases if needed
//        }
//    }
}

}
