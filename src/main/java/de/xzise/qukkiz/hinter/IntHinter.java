package de.xzise.qukkiz.hinter;

import java.util.Random;

import de.xzise.qukkiz.questions.QuestionInterface;

public class IntHinter extends FirstComeHinter<IntHinterSettings> {

    private final int ranges[];
    private final int value;
    private final QuestionInterface question;
    private int lastValue;
    private int hintNumber;
    private IntHinterSettings settings;
    
    public IntHinter(int value, IntHinterSettings settings, QuestionInterface question) {
        super(settings);
        this.value = value;
        this.question = question;
        this.hintNumber = 0;
        // Hint count
        this.ranges = new int[3];
        this.ranges[0] = this.settings.start;
        for (int i = 1; i < this.ranges.length; i++) {
            this.ranges[i] = this.ranges[i - 1] / this.settings.decrease;
        }
        this.lastValue = this.getHintValue();
    }
    
    private int getHintValue() {
        int range = (int) Math.ceil(value * (this.ranges[this.hintNumber] / 100.0));
        
        int newValue = new Random().nextInt(range);
        return newValue;
        
    }
    
    @Override
    public void nextHint() {
        this.hintNumber++;
        this.lastValue = this.getHintValue();
    }

    @Override
    public String getHint() {
        return this.lastValue + " +/-" + this.ranges[this.hintNumber] + "%";
    }

    @Override
    public QuestionInterface getQuestion() {
        return this.question;
    }

}