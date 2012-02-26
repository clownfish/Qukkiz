package de.xzise.qukkiz.questioner;

import java.util.List;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.QuestionInterface;

public interface Questioner {

    public enum AnswerResult {
        /** Correct answer and question finished. */
        FINISHED,
        /** Correct answer but question isn't finished. */
        NOT_FINISHED,
        /** Possible answer but maybe not correct. */
        VALID,
        /** Was valid but now not a valid answer anymore. */
        INVALIDATED,
        /** Impossible answer. */
        INVALID;
    }

    QuestionInterface getQuestion();

    Hinter<?> getHinter();

    AnswerResult putAnswer(Answer answer);

    List<Answer> getBestAnswers();
}
