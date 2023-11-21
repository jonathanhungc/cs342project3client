/**
 * FILE: GameInfo.java
 *
 * Class used for communication between client and server. GameInfo has different members used by the server
 * to send information of the game to the client, and used by the client to notify the server of its current actions.
 */

import java.io.Serializable;
import java.util.Arrays;

public class GameInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    char[] wordGuess; // It is empty at the start of each guess of a word, and the server updates constantly
    // depending on the letter sent by the client
    int lettersGuessed; // server updates how many letters have been guessed of the current word
    int misses; // server updates how many misses the client has
    int categoriesPassed; // number of categories completed
    String[] categories; // the server sends how many categories are left for the client to complete
    int[] wordsInCategories; // the server sends how many words are left in each category.
    String message; // used to specify what letter the client is sending, what category the client selected, etc.
    String flag; // flags that determine the type of request/response

    // constructor sets the flag of the object

    GameInfo() {}
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

    // set by the server to set the current names of the categories
    public void setCategories(String[] otherCategories) {
        categories = new String[otherCategories.length];
        categories = otherCategories.clone();
    }

    // set by the server to set the current number of words in each category
    public void setWordsInCategories(int[] letters) {
        wordsInCategories = Arrays.copyOf(letters, letters.length);
    }

    // used to set the message member
    public void setMessage(String message) {
        this.message = message;
    }
}