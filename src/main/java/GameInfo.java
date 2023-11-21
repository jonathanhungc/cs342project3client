import java.io.Serializable;
import java.util.Arrays;

public class GameInfo implements Serializable {
    private char[] wordGuess;
    private int lettersGuessed;
    private int misses;
    private int categoriesPassed;
    private String[] categories;
    private int[] wordsInCategories;
    private String message;
    private String flag;

    // New fields
    private String selectedCategory;
    private char guessedLetter;
    private boolean restartRequested;
    private boolean exitRequested;

    // private WordCategory selectedWordCategory; // Add this field

    // public WordCategory getSelectedWordCategory() {
    //     return selectedWordCategory;
    // }

    // public void setSelectedWordCategory(WordCategory selectedWordCategory) {
    //     this.selectedWordCategory = selectedWordCategory;
    // }

    public int getAttemptsLeft() {
        return 6 - misses;
    }

    public String getDisplayWord() {
        return String.valueOf(wordGuess); // Modify this based on your game logic
    }

    public GameInfo(String flag) {
        this.flag = flag;
    }

    public char[] getWordGuess() {
        return Arrays.copyOf(wordGuess, wordGuess.length);
    }

    public void setWordGuess(char[] wordGuess) {
        this.wordGuess = Arrays.copyOf(wordGuess, wordGuess.length);
    }

    public int getLettersGuessed() {
        return lettersGuessed;
    }

    public void setLettersGuessed(int lettersGuessed) {
        this.lettersGuessed = lettersGuessed;
    }

    public int getMisses() {
        return misses;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public int getCategoriesPassed() {
        return categoriesPassed;
    }

    public void setCategoriesPassed(int categoriesPassed) {
        this.categoriesPassed = categoriesPassed;
    }

    public String[] getCategories() {
        return Arrays.copyOf(categories, categories.length);
    }

    public void setCategories(String[] categories) {
        this.categories = Arrays.copyOf(categories, categories.length);
    }

    public int[] getWordsInCategories() {
        return Arrays.copyOf(wordsInCategories, wordsInCategories.length);
    }

    public void setWordsInCategories(int[] wordsInCategories) {
        this.wordsInCategories = Arrays.copyOf(wordsInCategories, wordsInCategories.length);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFlag() {
        return flag;
    }

    // New getters and setters
    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public char getGuessedLetter() {
        return guessedLetter;
    }

    public void setGuessedLetter(char guessedLetter) {
        this.guessedLetter = guessedLetter;
    }

    public boolean isRestartRequested() {
        return restartRequested;
    }

    public void setRestartRequested(boolean restartRequested) {
        this.restartRequested = restartRequested;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void setExitRequested(boolean exitRequested) {
        this.exitRequested = exitRequested;
    }

}
