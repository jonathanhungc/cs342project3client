import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;

	int port;
	
	private Consumer<Serializable> callback;
	
	Client(int port, Consumer<Serializable> call){

		this.port = port;
		callback = call;
	}
	
	public void run() {
		
		try {
			socketClient= new Socket("127.0.0.1",port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			 
			try {
				GameInfo data = (GameInfo) in.readObject();

				// server sent categories
				if (data.flag.equals("selectCategory")) {

				}

				// server sent word to guess. The guess is updated every time the user sends a letter
				else if (data.flag.equals("guess")) {


				}

				// server sent a flag that user won round
				else if (data.flag.equals("wonRound")) {


				}

				// server sent a flag that user lost round
				else if (data.flag.equals("lostRound")) {


				}

				// server sent a flag that user won game (won 3 categories)
				else if (data.flag.equals("wonGame")) {


				}

				// server sent a flag that user lost game
				else if (data.flag.equals("lostGame")) {


				}
			}
			catch(Exception e) {}
		}
	
    }

	// send request to server to send categories
	public void start() {
		
		try {
			GameInfo info = new GameInfo("sendCategory");
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// send category selected to the server, and request server to send word
	public void selectCategory(String categoryName) {

		try {
			GameInfo info = new GameInfo("selectedCategory");
			info.setMessage(categoryName);
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// send letter to the server
	public void sendLetter(String letter) {
		try {
			GameInfo info = new GameInfo("letter");
			info.setMessage(letter);
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// send request to the server to restart game
	public void restart() {
		try {
			GameInfo info = new GameInfo("restart");
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// send request to the server to exit game
	public void exit() {
		try {
			GameInfo info = new GameInfo("exit");
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
