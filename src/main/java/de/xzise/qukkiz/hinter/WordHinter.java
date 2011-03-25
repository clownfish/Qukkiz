package de.xzise.qukkiz.hinter;

import java.util.Random;
import java.util.regex.Pattern;

import de.xzise.qukkiz.questions.QuestionInterface;

public class WordHinter implements Hinter<WordHinterSettings> {
    
    private static final Pattern REPLACE_PATTERN = Pattern.compile("[a-zA-Z0-9]");

    private WordHinterSettings settings;
    private char[] hint;
    private final String hintResult;
    private final QuestionInterface question;
    
    public WordHinter(String hintResult, WordHinterSettings settings, QuestionInterface question) {
        this.setSettings(settings);
        this.hintResult = hintResult;
        this.hint = REPLACE_PATTERN.matcher(this.hintResult).replaceAll("*").toCharArray();
        this.question = question;
    }
    
    @Override
    public void nextHint() {
        this.nextHint(this.settings.lettersPerHint);
    }
    
    public void nextHint(int charsPerHint) {
        // Now many new chars should be revealed?
        int newChars = (int) Math.ceil(this.hintResult.length() / (double) charsPerHint);
        
        int asteriskCount = 0;
        for (char c : this.hint) {
            if (c == '*') {
                asteriskCount++;
            }
        }
        
        // At least one char has to be not revealed
        if (asteriskCount <= newChars) {
            newChars = asteriskCount - 1;
        }
        
        while (newChars > 0) {
            int replace = new Random().nextInt(this.hint.length);
            if (this.hint[replace] == '*') {
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
