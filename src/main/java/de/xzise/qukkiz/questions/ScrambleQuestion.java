package de.xzise.qukkiz.questions;

import java.util.Random;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.questioner.FirstComeQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class ScrambleQuestion implements QuestionInterface {

    private final static String QUESTION = "Unscramble this word: " + ChatColor.GREEN;
    
    public final String word;
    private final QukkizSettings settings;
    public String scrambled;
    
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
    public Questioner createHinter() {
        this.scrambled = scramble(word);
        return new FirstComeQuestioner(new WordHinter(this.word, this.settings.wordHinter), this);
    }

    @Override
    public String getQuestion() {
        if (this.scrambled == null) {
            this.scrambled = scramble(word);
        }
        return QUESTION + this.scrambled;
    }
    
}
