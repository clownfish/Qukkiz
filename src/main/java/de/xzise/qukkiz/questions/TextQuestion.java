package de.xzise.qukkiz.questions;

import de.xzise.MinecraftUtil;
import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.WordHinter;
import de.xzise.qukkiz.questioner.FirstComeQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class TextQuestion extends Question {

    public final AliasedAnswer answer;

    public TextQuestion(String question, QukkizSettings settings, String... answers) {
        this(question, settings, answers, new String[0]);
    }

    public TextQuestion(String question, QukkizSettings settings, String[] answers, String[] alternatives) {
        super(question, settings);
        this.answer = new AliasedAnswer(answers, alternatives);
    }

    @Override
    public Integer testAnswer(String answer) {
        return Question.parseAnswerTest(this.answer.check(answer));
    }

    @Override
    public String getAnswer() {
        StringBuilder result = new StringBuilder(this.answer.visibleAnswers[0]);
        for (int i = 1; i < this.answer.visibleAnswers.length; i++) {
            result.append(", " + this.answer.visibleAnswers[i]);
        }
        for (int i = 1; i < this.answer.aliases.length; i++) {
            result.append(", " + this.answer.aliases[i]);
        }
        return result.toString();
    }

    @Override
    public Questioner createQuestioner() {
        return new FirstComeQuestioner(new WordHinter(MinecraftUtil.getRandom(this.answer.visibleAnswers), this.settings.wordHinter), this);
    }
}
