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
    private Consumer<Serializable> callback;

    Client(int port, Consumer<Serializable> call) {
        this.port = port;
        callback = call;
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
                callback.accept(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCategoryRequest() {
        try {
            GameInfo info = new GameInfo("sendCategory");
            out.writeObject(info);
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
