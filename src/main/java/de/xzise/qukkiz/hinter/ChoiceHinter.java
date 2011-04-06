package de.xzise.qukkiz.hinter;

import java.util.Arrays;
import java.util.Random;

public class ChoiceHinter extends DefaultHinter<ChoiceHinterSettings> {

    private final String[] answers;
    private final int[] answerIdx;
    
    public ChoiceHinter(String[] answers, ChoiceHinterSettings settings) {
        super(settings);
        this.answers = answers;
        this.answerIdx = new int[answers.length];
        
        boolean[] used = new boolean[this.answers.length];
        Arrays.fill(used, false);
        Random r = new Random();
        for (int i = 0; i < this.answers.length; i++) {
            int idx;
            do {
                idx = r.nextInt(this.answers.length);
            } while (used[idx]);
            used[idx] = true;
            this.answerIdx[i] = idx;
        }
    }
    
    @Override
    public void nextHint() {        
        if (this.countVisibleAnswers() > this.getSettings().minimum) {
            Random r = new Random();
            int idx;
            do {
                idx = r.nextInt(this.answerIdx.length);
            } while (this.answerIdx[idx] == 0);
            this.answerIdx[idx] = -1;
        }
    }

    @Override
    public String getHint() {
        int count = this.countVisibleAnswers();
        
        StringBuilder choices = new StringBuilder();
        int offset = 0;
        for (int i : this.answerIdx) {
            if (i >= 0) {
                choices.append(this.answers[i]);
                offset++;
                if (offset < count) {
                    choices.append(", ");
                }
            }
        }
        return "Select one: " + choices.toString();
    }

    private int countVisibleAnswers() {
        int count = 0;
        for (int i : this.answerIdx) {
            if (i >= 0) {
                count++;
            }
        }
        return count;
    }
    
}
