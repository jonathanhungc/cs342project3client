import java.io.Serializable;

public class GameInfo implements Serializable {
    char[] userGuess; // server. It is empty at the start of each guess of a word, and the server updates constantly
                        // depending on the letter sent by the client
    int numLettersGuessed; // server updates how many letters are guessed per guess of a word.
    int numMisses; // server updates how many misses are per guess of a word
    int numConsecutiveMisses; // server keeps track of consecutive word misses
    int[] wordsCount; // server. Words left in each category. Should start with an array of 3 elements, 3 words in each category
    String[] categoriesCount; // server. The server sends how many categories are left for the client to complete
    String message; // server/client. This could be used to specify what letter the client is sending, what category the client
                    // selected, etc.
    String flag; // server/client flags

    GameInfo(String userFlag) {
        flag = userFlag;
    }
}
