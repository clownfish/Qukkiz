package de.xzise.qukkiz.questions;

import java.util.Random;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.hinter.WordHinterSettings;

public class ScrambleQuestion implements QuestionInterface {

    public final String word;
    private final QukkizSettings settings;
    public String question;
    
    public ScrambleQuestion(String word, QukkizSettings settings) {
        this.settings = settings;
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
        this.question = "Unscramble this word: " + ChatColor.GREEN + scramble(word);
        return new WordHinter(this.word, this.settings.wordHinter, this);
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public AnswerTypes getAnswerType() {
        return AnswerTypes.FIRST_COME;
    }
    
}
