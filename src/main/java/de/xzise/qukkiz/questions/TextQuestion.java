package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.hinter.WordHinterSettings;

public class TextQuestion extends Question {

    public final String[] answers;
    
    public TextQuestion(String question, QukkizSettings settings, String... answers) {
        super(question, settings);
        this.answers = answers;
    }
    
    @Override
    public boolean testAnswer(String answer) {
        for (String allowedAnswer : this.answers) {
            if (answer.equalsIgnoreCase(allowedAnswer)) {
                return true;
            }
        }
        return false;
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
    public Hinter<WordHinterSettings> createHinter() {
        return new WordHinter(this.answers[0], this.settings.wordHinter, this);
    }
}
