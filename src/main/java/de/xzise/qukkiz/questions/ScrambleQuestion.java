package de.xzise.qukkiz.questions;

import java.util.Random;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.hinter.WordHinterSettings;

public class ScrambleQuestion extends Question {

    public final String word;
    
    public ScrambleQuestion(String word, QukkizSettings settings) {
        super("Unscramble this word: " + scramble(word), settings);
        this.word = word;
    }

    @Override
    public boolean testAnswer(String answer) {
        return answer.equalsIgnoreCase(this.word);
    }

    public static String scramble(String word) {
        Random rand = new Random();
        char[] input = word.toCharArray();
        char[] result = new char[word.length()];
        boolean[] used = new boolean[word.length()];
        int i = 0;
        int length = word.length();
        while (i < length) {
            int newIdx = rand.nextInt(length);
            if (!used[newIdx]) {
                used[newIdx] = true;
                result[newIdx] = input[i];
                i++;
            }
        }        
        return new String(result);
    }

    @Override
    public String getAnswer() {
        return this.word;
    }

    @Override
    public Hinter<WordHinterSettings> createHinter() {
        return new WordHinter(this.word, this.settings.wordHinter, this);
    }
    
}
