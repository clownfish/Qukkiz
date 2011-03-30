package de.xzise.qukkiz.questions;

import java.util.Arrays;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.ChoiceHinter;
import de.xzise.qukkiz.questioner.BestGuessQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class MultipleChoiceQuestion extends Question {

    private final String[] answers;

    public MultipleChoiceQuestion(String question, QukkizSettings settings, String... answers) {
        super(question, settings);
        this.answers = answers;
    }

    public static MultipleChoiceQuestion create(String[] segments, QukkizSettings settings) {
        int offset = 0;
        // Ignore first element
        if (segments[0].equalsIgnoreCase("multiple choice")) {
            offset++;
        }
        String question = segments[offset];
        String[] answers = Arrays.copyOfRange(segments, offset + 1, segments.length);
        return new MultipleChoiceQuestion(question, settings, answers);
    }

    @Override
    public Integer testAnswer(String answer) {
        return Question.parseAnswerTest(this.answers[0].equalsIgnoreCase(answer));
    }

    @Override
    public Questioner createHinter() {
        return new BestGuessQuestioner(new ChoiceHinter(this.answers, this.settings.choiceHinter), this);
    }

    @Override
    public String getAnswer() {
        return this.answers[0];
    }

}
