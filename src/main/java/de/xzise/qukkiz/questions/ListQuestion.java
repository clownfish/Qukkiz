package de.xzise.qukkiz.questions;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.hinter.ListHinter;
import de.xzise.qukkiz.questioner.ListQuestioner;
import de.xzise.qukkiz.questioner.Questioner;

public class ListQuestion extends Question {

    private final AliasedAnswer[] answers;
    private final String answerText;

    public ListQuestion(String question, QukkizSettings settings, String... answers) {
        this(question, settings, AliasedAnswer.createArray(answers));
    }

    public ListQuestion(String question, QukkizSettings settings, AliasedAnswer... answers) {
        super(question, settings);
        this.answers = answers;
        boolean firstElement = true;
        StringBuilder builder = new StringBuilder(ChatColor.GREEN.toString());
        for (AliasedAnswer aliasedAnswer : this.answers) {
            for (String answer : aliasedAnswer.visibleAnswers) {
                if (!firstElement) {
                    builder.append(ChatColor.WHITE + ", " + ChatColor.GREEN);
                }
                builder.append(answer);
                firstElement = false;
            }
        }
        this.answerText = builder.append(ChatColor.WHITE).toString();
    }

    @Override
    public double testAnswer(String answer) {
        for (AliasedAnswer aliasedAnswer : this.answers) {
           if (aliasedAnswer.check(answer)) {
               return Question.parseAnswerTest(true);
           }
        }
        return Question.parseAnswerTest(false);
    }

    @Override
    public String getAnswer() {
        return this.answerText;
    }

    @Override
    public Questioner createQuestioner() {
        return new ListQuestioner(new ListHinter(this.answers, this.settings.listHinter), this);
    }

}
