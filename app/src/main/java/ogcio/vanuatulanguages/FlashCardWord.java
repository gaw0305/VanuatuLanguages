package ogcio.vanuatulanguages;

/**
 * Created by whitmorg on 22/09/2017.
 *
 */

public class FlashCardWord {
    private String word;
    private String knownWords;
    private String unknownWords;
    private String learningWords;
    private boolean stackComplete;

    public FlashCardWord(String word, String knownWords, String unknownWords, String learningWords,
                         boolean stackComplete) {
        this.word = word;
        this.knownWords = knownWords;
        this.unknownWords = unknownWords;
        this.learningWords = learningWords;
        this.stackComplete = stackComplete;
    }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }

    public String getKnownWords() { return knownWords; }

    public void setKnownWords(String knownWords) { this.knownWords = knownWords; }

    public String getUnknownWords() { return unknownWords; }

    public void setUnknownWords(String unknownWords) { this.unknownWords = unknownWords; }

    public String getLearningWords() { return learningWords; }

    public void setLearningWords(String learningWords) { this.learningWords = learningWords; }

    public boolean isStackComplete() { return stackComplete; }

    public void setStackComplete(boolean stackComplete) { this.stackComplete = stackComplete; }
}
