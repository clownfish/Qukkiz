package de.xzise.qukkiz.questioner;

import java.util.List;

import de.xzise.MinecraftUtil;
import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.ListHinter;
import de.xzise.qukkiz.questions.ListQuestion;
import de.xzise.qukkiz.questions.QuestionInterface;

public class ListQuestioner implements Questioner {

    private Answer lastAnswer = null;
    private final ListHinter hinter;
    private final ListQuestion question;

    public ListQuestioner(ListHinter hinter, ListQuestion question) {
        super();
        this.hinter = hinter;
        this.question = question;
    }

    @Override
    public QuestionInterface getQuestion() {
        return this.question;
    }

    @Override
    public Hinter<?> getHinter() {
        return this.hinter;
    }

    @Override
    public AnswerResult putAnswer(Answer answer) {
        final Boolean isCorrect = this.hinter.isCorrect(answer.answer);
        // TODO: Add setting to define mode "first wrong answer stops"
        if (isCorrect == null) {
            return AnswerResult.INVALID;
        } else if (isCorrect) {
            if (this.lastAnswer == null) {
                this.lastAnswer = answer;
            }
            this.hinter.nextHint(answer.answer);
            return this.hinter.getInvisibleCount() > 0 ? AnswerResult.NOT_FINISHED : AnswerResult.FINISHED;
        } else {
            return AnswerResult.INVALIDATED;
        }
    }

    @Override
    public List<Answer> getBestAnswers() {
        return MinecraftUtil.getOneElementList(this.lastAnswer);
    }
}
