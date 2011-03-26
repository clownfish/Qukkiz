package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;

public abstract class Question implements QuestionInterface {

    public final String question;
    protected final QukkizSettings settings;
    private final AnswerTypes type;
    
    protected Question(String question, QukkizSettings settings, AnswerTypes type) {
        this.question = question;
        this.settings = settings;
        this.type = type;
    }
    
    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public AnswerTypes getAnswerType() {
        return this.type;
    }
}
