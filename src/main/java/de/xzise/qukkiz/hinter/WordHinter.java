package de.xzise.qukkiz.hinter;

import java.util.Random;
import java.util.regex.Pattern;

public class WordHinter extends DefaultHinter<WordHinterSettings> {

    private static final Pattern REPLACE_PATTERN = Pattern.compile("[a-zA-Z0-9]");

    private char[] hint;
    private final int maskedCount;
    private final boolean[] masked;
    private final String hintResult;

    public WordHinter(String hintResult, WordHinterSettings settings) {
        super(settings);
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
    }

    @Override
    public void nextHint() {
        // Now many new chars should be revealed?
        int newChars = (int) Math.ceil(this.maskedCount / (double) this.getSettings().lettersPerHint);
        
        int asteriskCount = 0;
        for (boolean bool : this.masked) {
            if (bool) {
                asteriskCount++;
            }
        }
        
        // At least one char has to be not revealed
        if (asteriskCount < newChars + this.getSettings().minimumMasked) {
            newChars = asteriskCount - this.getSettings().minimumMasked;
        }
        
        while (newChars > 0) {
            int replace = new Random().nextInt(this.hint.length);
            if (this.masked[replace]) {
                this.masked[replace] = false;
                this.hint[replace] = this.hintResult.charAt(replace);
                newChars--;
            }
        }
    }

    @Override
    public String getHint() {
        return new String(this.hint);
    }

}
