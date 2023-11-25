import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ClientGUI extends Application {
    private Client clientConnection;
    private VBox categoriesList;
    private Stage primaryStage;
    private Scene currentGameScene;
    private Set<String> wonCategories = new HashSet<>();
    private Set<String> incorrectWords = new HashSet<>();
    private List<String> categoriesStatus = new ArrayList<>();
    private int port;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox portBox = createPortInputBox();

        Scene portScene = new Scene(portBox, 400, 200);
        primaryStage.setScene(portScene);
        primaryStage.setTitle("Enter Server Port");
        primaryStage.show();
    }

    private VBox createPortInputBox() {
        Label titleLabel = new Label("Enter Server Port:");
        TextField portField = new TextField();
        portField.setMaxWidth(150);

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(event -> {
            try {
                port = Integer.parseInt(portField.getText());
                initializeClient(primaryStage);
            } catch (NumberFormatException e) {
                showError("Invalid Port Number", "Please enter a valid port number.");
            }
        });

        VBox portBox = new VBox(20, titleLabel, portField, connectButton);
        portBox.setAlignment(Pos.CENTER);
        portBox.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");
        return portBox;
    }

    private void initializeClient(Stage primaryStage) {
        categoriesList = new VBox(20);

        clientConnection = new Client(port,
                data -> Platform.runLater(() -> handleCategories(data)),
                data -> Platform.runLater(() -> handleWord(data)),
                data -> Platform.runLater(() -> handleRoundWin(data)),
                data -> Platform.runLater(() -> handleRoundLoss(data)),
                data -> Platform.runLater(() -> handleGameWin(data)),
                data -> Platform.runLater(() -> handleGameLoss(data)), // Added this line
                data -> Platform.runLater(() -> handleError(data)));

        clientConnection.setClientGUI(this);
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
        exitBtn.setOnAction(e -> handleExitConfirmation());

        Label messageLabel = new Label("Welcome to the Game!");

        VBox box = new VBox(40, messageLabel, startBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial");

        return new Scene(box, 700, 800);
    }

    private Scene createCategoryScene(Stage primaryStage) {
        Label titleLabel = new Label("Choose a Category");

        VBox box = new VBox(40);
        box.getChildren().add(titleLabel);

        categoriesList.getChildren().forEach(node -> {
            Button categoryOption = (Button) node;
            String categoryText = categoryOption.getText();

//            if (categoriesStatus.contains(categoryText)) {
//                categoryOption.setDisable(true);
//            } else {
//                categoryOption.setDisable(false);
//            }

            categoryOption.setOnAction(e -> {
                clientConnection.sendSelectedCategory(categoryText);
                //categoriesStatus.add(selectedCategory);
            });
        });

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

        Label wordLabel = new Label(new String(info.wordGuess));
        wordLabel.setStyle("-fx-font-family: Arial;");

        TextField textField = new TextField();
        textField.setPromptText("Guess letter");
        textField.setMaxWidth(200);
        textField.setPrefHeight(60);
        textField.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-font-family: Arial;");
        textField.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() <= 1 ? change : null));

        Label attempts = new Label(6 - info.misses + " misses left");
        attempts.setStyle("-fx-font-family: Arial;");

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-font-family: Arial;");

        VBox box = new VBox(30, instructionLabel, wordLabel, textField, attempts, submitBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan;");

        submitBtn.setOnAction(e -> {
            char guess = textField.getText().charAt(0);
            clientConnection.sendLetter(String.valueOf(guess));
            textField.clear();
        });

        currentGameScene = new Scene(box, 800, 1000);

        return currentGameScene;
    }

    public void showRoundWinPopup(String word) {
        //Platform.runLater(() -> {
            //primaryStage.setScene(createCategoryScene(primaryStage));

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);

            Label messageLabel = new Label("Good Job! You guessed the word \"" + word + "\" correctly.");
            Button nextButton = new Button("Next");
            nextButton.setOnAction(e -> {
                popupStage.close();
                primaryStage.setScene(createCategoryScene(primaryStage));
            });

            VBox vbox = new VBox(20, messageLabel, nextButton);
            vbox.setAlignment(Pos.CENTER);
            vbox.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");
            Scene popupScene = new Scene(vbox, 300, 200);

            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        //});
    }

    public void showRoundLossPopup(String word) {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);

            Label messageLabel = new Label("Oops! You lost the round. The word was: " + word);
            Button nextButton = new Button("Next");
            nextButton.setOnAction(e -> {
                popupStage.close();
                primaryStage.setScene(createCategoryScene(primaryStage));
            });

            VBox vbox = new VBox(20, messageLabel, nextButton);
            vbox.setAlignment(Pos.CENTER);
            vbox.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");
            Scene popupScene = new Scene(vbox, 300, 200);

            popupStage.setScene(popupScene);
            popupStage.showAndWait();
    }

    private Scene createGameWinScene(String message, Stage primaryStage) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-family: Arial;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-family: Arial;");
        restartButton.setOnAction(e -> {
            clientConnection.sendRestartRequest();
            //primaryStage.setScene(createCategoryScene(primaryStage));
        });

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-family: Arial;");
        exitButton.setOnAction(e -> handleExitConfirmation());

        VBox box = new VBox(20, messageLabel, restartButton, exitButton);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");
        return new Scene(box, 300, 200);
    }

    private Scene createGameLossScene(String message, Stage primaryStage) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-family: Arial;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-family: Arial;");
        restartButton.setOnAction(e -> {
            clientConnection.sendRestartRequest();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-family: Arial;");
        exitButton.setOnAction(e -> handleExitConfirmation());

        VBox box = new VBox(20, messageLabel, restartButton, exitButton);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial;");
        return new Scene(box, 300, 200);
    }

    private void handleCategories(Serializable data) {

        GameInfo info = (GameInfo) data;
        categoriesList.getChildren().clear();

        for (String category : info.categories) {
            Button categoryOption = new Button(category);
            categoryOption.setOnAction(e -> {
                clientConnection.sendSelectedCategory(categoryOption.getText());
                //categoryOption.setDisable(true);
                //wonCategories.add(categoryOption.getText());
            });

            categoryOption.setStyle("-fx-font-family: Arial");
            categoriesList.getChildren().add(categoryOption);
        }

        primaryStage.setScene(createCategoryScene(primaryStage));
        }

    private void handleWord(Serializable data) {
        GameInfo info = (GameInfo) data;
        primaryStage.setScene(createGameScene(info, primaryStage));
    }

    private void handleRoundWin(Serializable data) {
        String word = ((GameInfo) data).message;
        showRoundWinPopup(word);
        clientConnection.sendCategoryRequest();
    }

    private void handleRoundLoss(Serializable data) {
        String word = ((GameInfo) data).message;
        showRoundLossPopup(word);
        clientConnection.sendCategoryRequest();
    }

    private void handleGameWin(Serializable data) {
        if (data instanceof GameInfo && ((GameInfo) data).flag.equals("wonGame")) {
            String message = "Congratulations! You won the game!";
            primaryStage.setScene(createGameWinScene(message, primaryStage));
            // Reset the client state when restarting the game
            clientConnection.resetGameState();
            clientConnection.clearWonCategories();
            clientConnection.clearCategoriesStatus();

            for (String category : categoriesStatus) {
                System.out.println(category);
            }            

        } else {
            System.out.println("Unexpected data type for game win: " + data.getClass());
        }
    }    
    
    private void handleGameLoss(Serializable data) {
        if (data instanceof GameInfo && ((GameInfo) data).flag.equals("lostGame")) {
            String message = "You lose! Better luck next time!";
            primaryStage.setScene(createGameLossScene(message, primaryStage));
            // Reset the client state when restarting the game
            clientConnection.resetGameState();
            clientConnection.clearWonCategories();
            clientConnection.clearCategoriesStatus();



        } else {
            System.out.println("Unexpected data type for game loss: " + data.getClass());
        }
    }    

    private void handleError(Serializable data) {
        if (data instanceof GameInfo) {
            GameInfo info = (GameInfo) data;
            showError("Error", "An error occurred: " + info.message);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the game?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clientConnection.sendExitRequest();
            Platform.exit();
        }
    }

    private void updateIncorrectWords(String word) {
        if (word != null && !word.isEmpty()) {
            incorrectWords.add(word);
        }
    }
}
