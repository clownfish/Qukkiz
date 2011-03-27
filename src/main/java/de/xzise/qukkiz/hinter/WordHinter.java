package de.xzise.qukkiz.hinter;

import java.util.Random;
import java.util.regex.Pattern;

import de.xzise.qukkiz.questions.QuestionInterface;

public class WordHinter implements Hinter<WordHinterSettings> {
    
    private static final Pattern REPLACE_PATTERN = Pattern.compile("[a-zA-Z0-9]");

    private WordHinterSettings settings;
    private char[] hint;
    private final int maskedCount;
    private final boolean[] masked;
    private final String hintResult;
    private final QuestionInterface question;
    
    public WordHinter(String hintResult, WordHinterSettings settings, QuestionInterface question) {
        this.setSettings(settings);
        this.hintResult = hintResult;
        this.masked = new boolean[this.hintResult.length()];
        
        int maskedCount = 0;
        this.hint = REPLACE_PATTERN.matcher(this.hintResult).replaceAll("*").toCharArray();
        for (int i = 0; i < this.hint.length; i++) {
            this.masked[i] = this.hint[i] != this.hintResult.charAt(i);
            if (this.masked[i]) {
                maskedCount++;
            }
        }
        this.maskedCount = maskedCount;
        
        this.question = question;
    }
    
    @Override
    public void nextHint() {
        // Now many new chars should be revealed?
        int newChars = (int) Math.ceil(this.maskedCount / (double) this.settings.lettersPerHint);
        
        int asteriskCount = 0;
        for (boolean bool : this.masked) {
            if (bool) {
                asteriskCount++;
            }
        }
        
        // At least one char has to be not revealed
        if (asteriskCount < newChars + this.settings.minimumMasked) {
            newChars = asteriskCount - this.settings.minimumMasked;
        }
        
        while (newChars > 0) {
            int replace = new Random().nextInt(this.hint.length);
            if (this.masked[replace]) {
                this.masked[replace] = true;
                this.hint[replace] = this.hintResult.charAt(replace);
                newChars--;
            }
        }       
    }

    @Override
    public String getHint() {
        return new String(this.hint);
    }

    @Override
    public QuestionInterface getQuestion() {
        return this.question;
    }

    @Override
    public void setSettings(WordHinterSettings settings) {
        this.settings = settings;
    }

}
