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
    }
    
    public void resetGameState() {
        // Reset the current word and incorrect words
        this.currentWord = null;
        this.incorrectWords.clear();
    }

    public Set<String> getWonCategories() {
        return wonCategories;
    }

    public void clearWonCategories() {
        wonCategories.clear();
    }
    public void clearCategoriesStatus() {
        categoriesStatus.clear();
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public String getCurrentWord() {
        return currentWord;
    }

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
//                        this.currentWord = data.message;
//                        incorrectWords.add(currentWord);
                        callbackRoundLoss.accept(data);
                        break;
                    case "wonGame":
                        callbackGameWin.accept(data);
                        break;
                    case "lostGame":
                        callbackGameLoss.accept(data); // Added this line
                        break;
                    case "error":
                        errorCallback.accept(data.message);
                        break;
                    // Add more cases as needed

                    default:
                        break;
                }

            } catch (Exception e) {
                errorCallback.accept("Connection lost. Please restart the application.");
                break;
            }
        }
    }

    public void sendCategoryRequest() {
        try {
            GameInfo info = new GameInfo("sendCategories");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending category request.");
        }
    }

    public void sendSelectedCategory(String categoryName) {
        try {
            GameInfo info = new GameInfo("selectedCategory");
            info.setMessage(categoryName);
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending selected category.");
        }
    }

    public void sendLetter(String letter) {
        try {
            GameInfo info = new GameInfo("letter");
            info.setMessage(letter);
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending letter.");
        }
    }

    public void sendRestartRequest() {
        try {
            GameInfo info = new GameInfo("restart");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending restart request.");
        }
    }

    public void sendExitRequest() {
        try {
            GameInfo info = new GameInfo("exit");
            out.writeObject(info);
        } catch (IOException e) {
            errorCallback.accept("Error sending exit request.");
        }
    }
}
