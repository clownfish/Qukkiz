package de.xzise.qukkiz.questioner;

import java.util.HashSet;
import java.util.Set;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.QuestionInterface;

public class BestGuessQuestioner implements Questioner {

    private final Set<Answer> answers = new HashSet<Answer>();
    private final Hinter<?> hinter;
    private final QuestionInterface question;
    
    public BestGuessQuestioner(Hinter<?> hinter, QuestionInterface question) {
        super();
        this.hinter = hinter;
        this.question = question;
    }

    @Override
    public Hinter<?> getHinter() {
        return this.hinter;
    }

    @Override
    public QuestionInterface getQuestion() {
        return this.question;
    }

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
