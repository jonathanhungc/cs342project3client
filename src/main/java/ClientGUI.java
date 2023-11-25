/*
 * Author: Yamaan Nandolia & Jonathan Hung
 * NetID: ynand3@uic.edu & jhung9@uic.edu
 * File Name: ClientGUI.java
 * Project Name: Multi-Threaded Server/Client Game
 * System: VSCode on Mac
 * File Description: JavaFX GUI for a Word Guess Game client.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * CientGUI: The following is a class that implements the entire GUI of the client side
 */
public class ClientGUI extends Application {
    private Client clientConnection;
    private VBox categoriesList;
    private Stage primaryStage;
    private Scene currentGameScene;
    private List<String> categoriesStatus = new ArrayList<>();
    private int port;

    /*
     * main: main function of the program and the entrypoint as well.
     */
    public static void main(String[] args) {
        launch(args);
    } // end of main()

    /*
     * start: Starts the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox portBox = createPortInputBox();

        Scene portScene = new Scene(portBox, 400, 200);
        primaryStage.setScene(portScene);
        primaryStage.setTitle("Word Guess Game");
        primaryStage.show();
    } // end of start()

    /**
     * createPortInputBox: Creates the VBox for entering the server port.
     * @return The VBox for entering the server port.
     */
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
    
        // Apply styling changes to the button
        connectButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1;");
    
        VBox portBox = new VBox(20, titleLabel, portField, connectButton);
        portBox.setAlignment(Pos.CENTER);
    
        // Apply styling changes to the overall VBox
        portBox.setStyle("-fx-background-color: cyan; -fx-font-family: 'Arial'; -fx-font-size: 14; ");
    
        return portBox;
    } // end of createPortInputBox()
    
    /**
     * initializeClient: Initializes the client connection and sets up the start scene.
     * @param primaryStage The primary stage for the JavaFX application.
     */
    private void initializeClient(Stage primaryStage) {
        categoriesList = new VBox(20);

        clientConnection = new Client(port,
                data -> Platform.runLater(() -> handleCategories(data)),
                data -> Platform.runLater(() -> handleWord(data)),
                data -> Platform.runLater(() -> handleRoundWin(data)),
                data -> Platform.runLater(() -> handleRoundLoss(data)),
                data -> Platform.runLater(() -> handleGameWin(data)),
                data -> Platform.runLater(() -> handleGameLoss(data)),
                data -> Platform.runLater(() -> handleError(data)));

        clientConnection.setClientGUI(this);
        clientConnection.start();

        Scene start = createStartScene(primaryStage);
        primaryStage.setScene(start);
        primaryStage.show();
    } // end of initializeClient()

    /**
     * createStartScene: Creates the start scene with welcome message and buttons.
     * @param primaryStage The primary stage for the JavaFX application.
     * @return The Scene for the start screen.
     */
    private Scene createStartScene(Stage primaryStage) {
        Button startBtn = new Button("Start");
        startBtn.setOnAction(e -> {
            clientConnection.sendCategoryRequest();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });
    
        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> primaryStage.close());
    
        Label messageLabel = new Label("Welcome to the Word Guess Game!");
    
        // Apply styling changes to the message label
        messageLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");
    
        // Apply styling changes to the buttons
        startBtn.setStyle("-fx-font-size: 24; -fx-background-color: white; -fx-text-fill: black;"); // Increase font size for the Start button
        exitBtn.setStyle("-fx-font-size: 24; -fx-background-color: white; -fx-text-fill: black; ");  // Increase font size for the Exit button
    
        VBox box = new VBox(20, messageLabel, new Region(), startBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
    
        // Apply styling changes to the overall VBox
        box.setStyle("-fx-background-color: cyan; -fx-font-family: 'Arial'; -fx-font-size: 14;");
    
        // Center the message label at the top
        VBox.setMargin(messageLabel, new Insets(50, 0, 0, 0));
    
        // Center the buttons in the middle
        VBox.setMargin(startBtn, new Insets(0, 0, 20, 0));
        VBox.setMargin(exitBtn, new Insets(0, 0, 20, 0));
    
        return new Scene(box, 700, 800);
    } // end of createStartScene()
    

    /**
     * createCategoryScene: Creates the scene for selecting a category.
     * @param primaryStage The primary stage for the JavaFX application.
     * @return The Scene for selecting a category.
     */
    private Scene createCategoryScene(Stage primaryStage) {
        Label titleLabel = new Label("Choose a Category!");
        titleLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");
    
        VBox box = new VBox(20);
        box.getChildren().add(titleLabel);
    
        categoriesList.getChildren().forEach(node -> {
            Button categoryOption = (Button) node;
            String categoryText = categoryOption.getText();
            categoryOption.setOnAction(e -> {
                clientConnection.sendSelectedCategory(categoryText);
            });
    
            // Apply styling changes to the buttons
            categoryOption.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24; -fx-background-color: white; -fx-text-fill: black;");
        });
    
        // Adjust the spacing to center the buttons vertically
        VBox.setMargin(titleLabel, new Insets(50, 0, 20, 0)); // Increase top margin
        VBox.setMargin(categoriesList, new Insets(0, 0, 20, 0)); // Decrease bottom margin

        
        categoriesList.setAlignment(Pos.CENTER);
        box.getChildren().addAll(categoriesList);
        box.setAlignment(Pos.CENTER);
    
        // Apply styling changes to the overall VBox
        box.setStyle("-fx-background-color: cyan; -fx-font-family: 'Arial'; -fx-font-size: 14;");
    
        return new Scene(box, 700, 800);
    } // end of createCategoryScene()

    /**
    * createGameScene: Creates the game scene with word guessing interface.
    * @param info           The GameInfo object containing game data.
    * @param primaryStage   The primary stage for the JavaFX application.
    * @return The Scene for the game interface.
    */
    private Scene createGameScene(GameInfo info, Stage primaryStage) {
        Label instructionLabel = new Label("Guess a letter of the word!");
        instructionLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");

        for (int i = 0; i < info.wordGuess.length; i++) {
            if (info.wordGuess[i] == '\0') {
                info.wordGuess[i] = '_';
            }
        }

        Label wordLabel = new Label(new String(info.wordGuess));
        wordLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 30; -fx-font-weight: bold;");

        TextField textField = new TextField();
        textField.setPromptText("Guess letter");
        textField.setMaxWidth(200);
        textField.setPrefHeight(60);
        textField.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-font-family: Arial; -fx-font-size: 16;");

        // Use TextFormatter to limit input to one lowercase letter or empty string (for backspace/delete)
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText().toLowerCase();
            if (newText.matches("[a-z]") || newText.isEmpty()) {
                return change;
            } else {
                return null;
            }
        });

        textField.setTextFormatter(formatter);

        Label attempts = new Label(6 - info.misses + " misses left");
        attempts.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-text-fill: black");

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");

        // Message VBox
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);
        Label messageLabel = new Label("Please enter lowercase letters only. Any other character will lead to a miss penalty.");
        messageLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 15; -fx-text-fill: black");

        messageBox.getChildren().add(messageLabel);

        VBox box = new VBox(20, instructionLabel, wordLabel, textField, attempts, submitBtn, messageBox);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial; -fx-font-size: 14;");

        submitBtn.setOnAction(e -> {
            String guess = textField.getText();
            if (!guess.isEmpty()) {
                char lowercaseGuess = guess.charAt(0);
                clientConnection.sendLetter(String.valueOf(lowercaseGuess));
                textField.clear();
            }
        });

        currentGameScene = new Scene(box, 800, 1000);

        return currentGameScene;
    } // end of createGameScene()

    

    /**
     * showRoundWinPopup: Shows a popup for a round win.
     * @param word The word that was guessed correctly.
     */
    public void showRoundWinPopup(String word) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
    
        Label messageLabel = new Label("Good Job! You guessed the word \"" + word + "\" correctly.");
        messageLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 24; -fx-font-weight: bold;");
    
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
    
        nextButton.setOnAction(e -> {
            popupStage.close();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });
    
        VBox vbox = new VBox(20, messageLabel, nextButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: cyan; -fx-font-family: Arial; -fx-font-size: 14;");
        
        Scene popupScene = new Scene(vbox, 650, 450);
    
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    } // end of showRoundWinPopup()
    
    /**
     * showRoundLossPopup: Shows a popup for a round loss.
     * @param word The correct word for the round.
     */
    public void showRoundLossPopup(String word) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
    
        Label messageLabel = new Label("Oops! You lost the round. The word was: " + word);
        messageLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 24; -fx-font-weight: bold;");
    
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
    
        nextButton.setOnAction(e -> {
            popupStage.close();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });
    
        VBox vbox = new VBox(20, messageLabel, nextButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: cyan; -fx-font-family: Arial; -fx-font-size: 14;");
        Scene popupScene = new Scene(vbox, 650, 450);
    
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    } // end of showRoundLossPopup()
    
    /**
     * createGameWinScene: Creates the scene for the game win message.
     * @param message The message to be displayed.
     * @param primaryStage The primary stage for the JavaFX application.
     * @return The Scene for the game win message.
     */
    private Scene createGameWinScene(String message, Stage primaryStage) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 24; -fx-font-weight: bold;");
    
        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
    
        restartButton.setOnAction(e -> {
            clientConnection.sendRestartRequest();
        });
    
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
        exitButton.setOnAction(e -> primaryStage.close());
    
        VBox box = new VBox(20, messageLabel, restartButton, exitButton);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial; -fx-font-size: 14;");
        return new Scene(box, 650, 450);
    } // end of createGameWinScene()
    
    /**
     * createGameLossScene: Creates the scene for the game loss message.
     * @param message The message to be displayed.
     * @param primaryStage The primary stage for the JavaFX application.
     * @return The Scene for the game loss message.
     */
    private Scene createGameLossScene(String message, Stage primaryStage) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 24; -fx-font-weight: bold;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
        restartButton.setOnAction(e -> {
            clientConnection.sendRestartRequest();
            primaryStage.setScene(createCategoryScene(primaryStage));
        });

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-background-color: white; -fx-text-fill: black;");
        exitButton.setOnAction(e -> primaryStage.close());

        VBox box = new VBox(20, messageLabel, restartButton, exitButton);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: cyan; -fx-font-family: Arial; -fx-font-size: 14;");
        return new Scene(box, 650, 450);
    } // end of createGameLossScene()

    /**
     * handleCategories: Handles the reception of categories from the server.
     * @param data The serialized data containing game information.
     */
    private void handleCategories(Serializable data) {

        GameInfo info = (GameInfo) data;
        categoriesList.getChildren().clear();

        for (String category : info.categories) {
            Button categoryOption = new Button(category);
            categoryOption.setOnAction(e -> {
                clientConnection.sendSelectedCategory(categoryOption.getText());
            });

            categoryOption.setStyle("-fx-font-family: Arial");
            categoriesList.getChildren().add(categoryOption);
        }

        primaryStage.setScene(createCategoryScene(primaryStage));
    } // end of handleCategories()

    /**
     * handleWord: Handles the reception of a word from the server.
     * @param data The serialized data containing game information.
     */
    private void handleWord(Serializable data) {
        GameInfo info = (GameInfo) data;
        primaryStage.setScene(createGameScene(info, primaryStage));
    } // end of handleWord()

    /**
     * handleRoundWin: Handles the reception of a round win from the server.
     * @param data The serialized data containing game information.
     */
    private void handleRoundWin(Serializable data) {
        String word = ((GameInfo) data).message;
        showRoundWinPopup(word);
        clientConnection.sendCategoryRequest();
    } // end of handleRoundWin()

    /**
     * handleRoundLoss: Handles the reception of a round loss from the server.
     * @param data The serialized data containing game information.
     */
    private void handleRoundLoss(Serializable data) {
        String word = ((GameInfo) data).message;
        showRoundLossPopup(word);
        clientConnection.sendCategoryRequest();
    } // end of handleRoundLoss()

    /**
     * handleGameWin: Handles the reception of a game win from the server.
     * @param data The serialized data containing game information.
     */
    private void handleGameWin(Serializable data) {
        if (data instanceof GameInfo && ((GameInfo) data).flag.equals("wonGame")) {
            String message = "Congratulations! You won the game!";
            primaryStage.setScene(createGameWinScene(message, primaryStage));
            clientConnection.resetGameState();
            clientConnection.clearWonCategories();
            clientConnection.clearCategoriesStatus();

            for (String category : categoriesStatus) {
                System.out.println(category);
            }            

        } else {
            System.out.println("Unexpected data type for game win: " + data.getClass());
        }
    } // end of handleGameWin()
    
    /**
     * handleGameLoss: Handles the reception of a game loss from the server.
     * @param data The serialized data containing game information.
     */
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
    } // end of handleGameLoss()

    /**
     * handleError: Handles the reception of an error message from the server.
     * @param data The serialized data containing game information.
    */
    private void handleError(Serializable data) {
        if (data instanceof GameInfo) {
            GameInfo info = (GameInfo) data;
            showError("Error", "An error occurred: " + info.message);
        }
    } // end of handleError()

    /**
     * showError: Shows an error alert dialog with the given title and message.
     * @param title The title of the error dialog.
     * @param message The error message to be displayed.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } // end of showError()

} // end of ClientGUI class
