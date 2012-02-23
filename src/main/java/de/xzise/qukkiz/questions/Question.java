package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.QukkizSettings;

public abstract class Question implements QuestionInterface {

    public final String question;
    protected final QukkizSettings settings;
    private final int maximumHints;

    protected Question(String question, QukkizSettings settings, int maximumHints) {
        this.question = question;
        this.settings = settings;
        this.maximumHints = maximumHints;
    }

    protected Question(String question, QukkizSettings settings) {
        this(question, settings, -1);
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public int getMaximumHints() {
        return this.maximumHints;
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
