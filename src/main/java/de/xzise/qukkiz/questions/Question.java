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
}
