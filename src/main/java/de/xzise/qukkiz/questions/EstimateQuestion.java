package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.IntHinter;
import de.xzise.qukkiz.hinter.IntHinterSettings;

public class EstimateQuestion extends Question {

    private final int answer;
    
    public EstimateQuestion(String question, QukkizSettings settings, int answer) {
        super(question, settings, AnswerTypes.FIRST_COME);
        this.answer = answer;
    }

    @Override
    public boolean testAnswer(String answer) {
        try {
            return Integer.parseInt(answer) == this.answer;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    public String getAnswer() {
        return Integer.toString(answer);
    }

    @Override
    public Hinter<IntHinterSettings> createHinter() {
        return new IntHinter(this.answer, this.settings.intHinter, this);
    }

}
