package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.questioner.FirstComeQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class TextQuestion extends Question {

    public final String[] answers;
    
    public TextQuestion(String question, QukkizSettings settings, String... answers) {
        super(question, settings);
        this.answers = answers;
    }
    
    @Override
    public Integer testAnswer(String answer) {
        for (String allowedAnswer : this.answers) {
            if (answer.equalsIgnoreCase(allowedAnswer)) {
                return 0;
            }
        }
        return null;
    }

    @Override
    public String getAnswer() {
        StringBuilder result = new StringBuilder(this.answers[0]);
        for (int i = 1; i < this.answers.length; i++) {
            result.append(", " + this.answers[i]);
        }
        return result.toString();
    }

    @Override
    public Questioner createHinter() {
        return new FirstComeQuestioner(new WordHinter(this.answers[0], this.settings.wordHinter), this);
    }
}
