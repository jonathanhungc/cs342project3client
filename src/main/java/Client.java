/*
 * Author: Yamaan Nandolia & Jonathan Hung
 * NetID: ynand3@uic.edu & jhung9@uic.edu
 * File Name: Client.java
 * Project Name: Multi-Threaded Server/Client Game
 * System: VSCode on Mac
 * File Description: Client class for the Word Guess Game.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/*
 * Client: The following is a class for the Client connection
 */
public class Client extends Thread {
    private Socket socketClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int port;
    private Consumer<Serializable> callbackCategories;
    private Consumer<Serializable> callbackWord;
    private Consumer<Serializable> callbackRoundWin;
    private Consumer<Serializable> callbackRoundLoss;
    private Consumer<Serializable> callbackGameWin;
    private Consumer<Serializable> callbackGameLoss; // Added this line
    private Consumer<Serializable> errorCallback;
    private ClientGUI clientGUI;
    private String currentWord;
    private Set<String> incorrectWords = new HashSet<>();
    private Set<String> wonCategories = new HashSet<>();
    private List<String> categoriesStatus = new ArrayList<>();

    /**
     * Client: Constructor for the Client class.
     * @param port              The port to connect to.
     * @param callbackCategories Callback function for handling category data.
     * @param callbackWord      Callback function for handling word data.
     * @param callbackRoundWin  Callback function for handling round win data.
     * @param callbackRoundLoss Callback function for handling round loss data.
     * @param callbackGameWin   Callback function for handling game win data.
     * @param callbackGameLoss  Callback function for handling game loss data.
     * @param errorCallback     Callback function for handling errors.
     */
    public Client(int port, Consumer<Serializable> callbackCategories, Consumer<Serializable> callbackWord,
                  Consumer<Serializable> callbackRoundWin, Consumer<Serializable> callbackRoundLoss,
                  Consumer<Serializable> callbackGameWin, Consumer<Serializable> callbackGameLoss, // Added this line
                  Consumer<Serializable> errorCallback) {
        this.port = port;
        this.callbackCategories = callbackCategories;
        this.callbackWord = callbackWord;
        this.callbackRoundWin = callbackRoundWin;
        this.callbackRoundLoss = callbackRoundLoss;
        this.callbackGameWin = callbackGameWin;
        this.callbackGameLoss = callbackGameLoss; // Added this line
        this.errorCallback = errorCallback;


        try {
            socketClient = new Socket("127.0.0.1", port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        } catch (Exception e) {
            errorCallback.accept("Error connecting to the server.");
        }
    } // end of Client()
    
    /*
     * resetGameState: Resets the game state by clearing the current word and incorrect words.
     */
    public void resetGameState() {
        // Reset the current word and incorrect words
        this.currentWord = null;
        this.incorrectWords.clear();
    } // end of resetGameState()

    /**
     * getWonCategories: Getter for the set of won categories.
     * @return The set of won categories.
     */
    public Set<String> getWonCategories() {
        return wonCategories;
    } // end of getWonCategories()

    /*
     * clearWonCategories: Clears the set of won categories.
     */
    public void clearWonCategories() {
        wonCategories.clear();
    } // end of clearWonCategories()

    /*
     * clearCategoriesStatus: Clears the list of categories' status.
     */
    public void clearCategoriesStatus() {
        categoriesStatus.clear();
    } // end of clearCategoriesStatus()

    /**
     * setClientGUI: Setter for the associated ClientGUI for the client.
     * @param clientGUI The associated ClientGUI.
     */
        public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    } // end of setClientGUI()

    /**
     * getCurrentWord: Getter for the current word.
     * @return The current word.
     */
    public String getCurrentWord() {
        return currentWord;
    } // end of getCurrentWord()

    /*
     * run: The main run method of the client thread. Listens for server messages and calls appropriate callbacks.
     */
    public void run() {
        while (true) {
            try {
                GameInfo data = (GameInfo) in.readObject();

                switch (data.flag) {
                    case "selectCategory":
                        callbackCategories.accept(data);
                        break;
                    case "guess":
                        callbackWord.accept(data);
                        break;
                    case "wonRound":
                        callbackRoundWin.accept(data);
                        break;
                    case "lostRound":
                        callbackRoundLoss.accept(data);
                        break;
                    case "wonGame":
                        callbackGameWin.accept(data);
                        break;
                    case "lostGame":
                        callbackGameLoss.accept(data);
                        break;
                    case "error":
                        errorCallback.accept(data.message);
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                errorCallback.accept("Connection lost. Please restart the application.");
                break;
            }
        }
    } // end of run()

    /*
     * sendCategoryRequest: Sends a category request to the server.
     */
    public void sendCategoryRequest() {
        try {
            GameInfo info = new GameInfo("sendCategories");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending category request.");
        }
    } // end of sendCategoryRequest()

    /**
     * sendSelectedCategory: Sends the selected category to the server.
     * @param categoryName The selected category.
     */
    public void sendSelectedCategory(String categoryName) {
        try {
            GameInfo info = new GameInfo("selectedCategory");
            info.setMessage(categoryName);
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending selected category.");
        }
    } // end of sendSelectedCategory()

    /**
     * sendLetter: Sends a guessed letter to the server.
     * @param letter The guessed letter.
     */
    public void sendLetter(String letter) {
        try {
            GameInfo info = new GameInfo("letter");
            info.setMessage(letter);
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending letter.");
        }
    } // end of sendLetter()

    /*
     * sendRestartRequest: Sends a restart request to the server.
     */
    public void sendRestartRequest() {
        try {
            GameInfo info = new GameInfo("restart");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending restart request.");
        }
    } // end of sendRestartRequest()

    /*
     * sendExitRequest: Sends an exit request to the server.
     */
    public void sendExitRequest() {
        try {
            GameInfo info = new GameInfo("exit");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending exit request.");
        }
    } // end of sendExitRequest()
} // end of Client class
