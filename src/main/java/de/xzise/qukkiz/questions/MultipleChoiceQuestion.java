package de.xzise.qukkiz.questions;

import java.util.Arrays;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.ChoiceHinter;
import de.xzise.qukkiz.hinter.ChoiceHinterSettings;
import de.xzise.qukkiz.hinter.Hinter;

public class MultipleChoiceQuestion extends Question {

    private final String[] answers;

    public MultipleChoiceQuestion(String question, QukkizSettings settings, String... answers) {
        super(question, settings, AnswerTypes.BEST_GUESS);
        this.answers = answers;
    }

    public static MultipleChoiceQuestion create(String[] segments, QukkizSettings settings) {
        int offset = 0;
        // Ignore first element
        if (segments[0].equalsIgnoreCase("multiple choice")) {
            offset++;
        }
        String question = segments[offset];
        String[] answers = Arrays.copyOfRange(segments, offset + 1, segments.length - offset);
        return new MultipleChoiceQuestion(question, settings, answers);
    }

    @Override
    public boolean testAnswer(String answer) {
        return this.answers[0].equalsIgnoreCase(answer);
    }

    @Override
    public Hinter<ChoiceHinterSettings> createHinter() {
        return new ChoiceHinter(this.answers, this.settings.choiceHinter, this);
    }

    @Override
    public String getAnswer() {
        return this.answers[0];
    }

}
