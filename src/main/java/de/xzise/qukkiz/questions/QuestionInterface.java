package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.questioner.Questioner;

public interface QuestionInterface {
    
    /**
     * Returns if the answer is correct.
     * @param answer Tested answer.
     * @return If it return null the answer is incorrect, if 0 the answer is perfect. Otherwise it define the difference to the answer.
     */
    Integer testAnswer(String answer);
    
    String getAnswer();
    
    Questioner createHinter();
    
    String getQuestion();
        
}
