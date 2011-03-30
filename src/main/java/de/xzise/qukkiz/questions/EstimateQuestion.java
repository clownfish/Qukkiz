package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.IntHinter;
import de.xzise.qukkiz.questioner.FirstComeQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class EstimateQuestion extends Question {

    private final int answer;
    
    public EstimateQuestion(String question, QukkizSettings settings, int answer) {
        super(question, settings);
        this.answer = answer;
    }

    @Override
    public Integer testAnswer(String answer) {
        try {
            int answerInt = Integer.parseInt(answer);            
            return Math.abs(this.answer - answerInt);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAnswer() {
        return Integer.toString(answer);
    }

    @Override
    public Questioner createHinter() {
        return new FirstComeQuestioner(new IntHinter(this.answer, this.settings.intHinter), this);
    }

}
