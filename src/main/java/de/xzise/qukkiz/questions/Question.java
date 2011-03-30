package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;

public abstract class Question implements QuestionInterface {

    public final String question;
    protected final QukkizSettings settings;
    
    protected Question(String question, QukkizSettings settings) {
        this.question = question;
        this.settings = settings;
    }
    
    @Override
    public String getQuestion() {
        return this.question;
    }
    
    public static Integer parseAnswerTest(boolean bool) {
        if (bool) {
            return 0;
        } else {
            return null;
        }
    }
    
    public static boolean parseAnswerTest(Integer integer) {
        if (integer == null || integer != 0) {
            return false;
        } else {
            return true;
        }
    }
}
