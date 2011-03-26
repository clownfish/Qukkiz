package de.xzise.qukkiz.hinter;

import de.xzise.qukkiz.questions.QuestionInterface;

public interface Hinter<Settings extends HinterSettings> {

    void nextHint();
    
    String getHint();
    
    QuestionInterface getQuestion();
    
    void setSettings(Settings settings);
    
    boolean putAnswer(Answer answer);
    
    Answer getBestAnswer();
    
}
