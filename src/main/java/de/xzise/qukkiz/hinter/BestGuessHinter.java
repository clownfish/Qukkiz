package de.xzise.qukkiz.hinter;

import java.util.HashSet;
import java.util.Set;

public abstract class BestGuessHinter<S extends HinterSettings> extends DefaultHinter<S> {

    public BestGuessHinter(S settings) {
        super(settings);
    }

    private Set<Answer> answers = new HashSet<Answer>();

    @Override
    public boolean putAnswer(Answer answer) {
        this.answers.remove(answer);
        this.answers.add(answer);
        //TODO: Cancel if last answer?
        return false;
    }

    @Override
    public Answer getBestAnswer() {
        Answer bestAnswer = null;
        for (Answer answer : this.answers) {
            if (this.getQuestion().testAnswer(answer.answer) && (bestAnswer == null || bestAnswer.time > answer.time)) {
                bestAnswer = answer;
            }
        }
        return bestAnswer;
    }

}
