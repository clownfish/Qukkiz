package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.questioner.Questioner;

public interface QuestionInterface {
    
    boolean testAnswer(String answer);
    
    String getAnswer();
    
    Questioner createHinter();
    
    String getQuestion();
        
}
