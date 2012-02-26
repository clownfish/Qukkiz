package de.xzise.qukkiz.questions;

import java.text.DecimalFormat;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.NumberHinter;
import de.xzise.qukkiz.questioner.BestGuessQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class EstimateQuestion extends Question {

    private final double answer;
    private final DecimalFormat format;

    public EstimateQuestion(final String question, final QukkizSettings settings, final double answer, final DecimalFormat format) {
        super(question, settings);
        this.answer = answer;
        this.format = format;
    }

    @Override
    public double testAnswer(String answer) {
        try {
            double answerDouble = Double.parseDouble(answer);
            return Math.abs(this.answer - answerDouble);
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

    @Override
    public String getAnswer() {
        return Double.toString(answer);
    }

    @Override
    public Questioner createQuestioner() {
        return new BestGuessQuestioner(new NumberHinter(this.answer, this.settings.intHinter, this.format), this, true);
    }

}
