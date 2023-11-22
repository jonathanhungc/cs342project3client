import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    private Socket socketClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int port;
    private Consumer<Serializable> callbackCategories;

    private Consumer<Serializable> callbackWord;


    Client(int port) {
        this.port = port;
    }

    public void setCallbackCategories(Consumer<Serializable> call) {
        callbackCategories = call;
    }

    public void setCallbackWord(Consumer<Serializable> call) {
        callbackWord = call;
    }

    public void run() {
        try {
            socketClient = new Socket("127.0.0.1", port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                GameInfo data = (GameInfo) in.readObject();

                if (data.flag.equals("selectCategory")) {
                    for (int i = 0; i < data.categories.length; i++) {
                        callbackCategories.accept(data.categories[i]);
                    }
                }

                else if (data.flag.equals("guess")) {
                    callbackWord.accept(data);
                }

                else if (data.flag.equals("")) {

                }

                else if (data.flag.equals("")) {

                }

                else if (data.flag.equals("")) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCategoryRequest() {
        try {
            GameInfo info = new GameInfo("sendCategories");
            out.writeObject(info);
            //System.out.println("sending request");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSelectedCategory(String categoryName) {
        try {
            GameInfo info = new GameInfo("selectedCategory");
            info.setMessage(categoryName);
            out.writeObject(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLetter(String letter) {
        try {
            GameInfo info = new GameInfo("letter");
            info.setMessage(letter);
            out.writeObject(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRestartRequest() {
        try {
            GameInfo info = new GameInfo("restart");
            out.writeObject(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendExitRequest() {
        try {
            GameInfo info = new GameInfo("exit");
            out.writeObject(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
