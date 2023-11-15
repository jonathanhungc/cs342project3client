import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameInfo implements Serializable {
    char[] wordGuess; // server. It is empty at the start of each guess of a word, and the server updates constantly
    // depending on the letter sent by the client
    int lettersGuessed; // server updates how many letters are guessed per guess of a word.
    int misses; // server updates how many misses are per guess of a word

    int categoriesPassed; // number of categories completed
    String[] categories; // server. The server sends how many categories are left for the client to complete
    String message; // server/client. This could be used to specify what letter the client is sending, what category the client
    // selected, etc.
    String flag; // server/client flags

    GameInfo(String flag) {
        this.flag = flag;
    }

    // mostly set by the server
    public void setElements(char[] word, int lettersGuessed, int misses, int categoriesPassed) {
        wordGuess = Arrays.copyOf(word, word.length);
        this.lettersGuessed = lettersGuessed;
        this.misses = misses;
        this.categoriesPassed = categoriesPassed;
    }

    public void setCategories(String[] otherCategories) {
        categories = new String[otherCategories.length];
        categories = otherCategories.clone();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
