package de.xzise.qukkiz.questioner;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.QuestionInterface;

public interface Questioner {

    QuestionInterface getQuestion();
    
    Hinter<?> getHinter();
    
    boolean putAnswer(Answer answer);
    
    Answer getBestAnswer();
    
}
