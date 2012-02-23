package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.ChoiceHinter;
import de.xzise.qukkiz.questioner.BestGuessQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class MultipleChoiceQuestion extends Question {

    private final AliasedAnswer[] answers;

    public MultipleChoiceQuestion(String question, QukkizSettings settings, AliasedAnswer... answers) {
        super(question, settings);
        this.answers = answers;
    }

    public MultipleChoiceQuestion(String question, QukkizSettings settings, String... answers) {
        this(question, settings, AliasedAnswer.createArray(answers));
    }

    @Override
    public Integer testAnswer(String answer) {
        Integer result = 0;
        for (int i = 0; i < this.answers.length; i++) {
            if (this.answers[i].check(answer)) {
                return result;
            }
            result = Integer.MAX_VALUE;
        }
        return null;
    }

    @Override
    public Questioner createQuestioner() {
        return new BestGuessQuestioner(new ChoiceHinter(AliasedAnswer.getVisibleAnswers(answers), this.settings.choiceHinter), this, false);
    }

    @Override
    public String getAnswer() {
        return this.answers[0].visibleAnswers[0];
    }

}
