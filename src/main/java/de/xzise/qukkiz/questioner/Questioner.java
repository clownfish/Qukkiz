package de.xzise.qukkiz.questioner;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.QuestionInterface;

public interface Questioner {

    public enum AnswerResult {
        /** Correct answer and question finished. */
        CORRECT,
        /** Possible answer but maybe not correct. */
        VALID,
        /** Impossible answer. */
        INVALID;
    }
    
    QuestionInterface getQuestion();
    
    Hinter<?> getHinter();
    
    AnswerResult putAnswer(Answer answer);
    
    Answer getBestAnswer();
    
}
