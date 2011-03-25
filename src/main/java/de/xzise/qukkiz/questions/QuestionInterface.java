package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.HinterSettings;

public interface QuestionInterface {

    boolean testAnswer(String answer);
    
    String getAnswer();
    
    Hinter<? extends HinterSettings> createHinter();
    
    String getQuestion();
    
}
