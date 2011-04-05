package de.xzise.qukkiz.questioner;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.Question;
import de.xzise.qukkiz.questions.QuestionInterface;

public class FirstComeQuestioner implements Questioner {

    private Answer correctAnswer = null;
    private final Hinter<?> hinter;
    private final QuestionInterface question;
    
    public FirstComeQuestioner(Hinter<?> hinter, QuestionInterface question) {
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
    public AnswerResult putAnswer(Answer answer) {
        if (this.correctAnswer == null && Question.parseAnswerTest(this.getQuestion().testAnswer(answer.answer))) {
            this.correctAnswer = answer;
        }
        if (this.correctAnswer == null) {
            return AnswerResult.INVALID;
        } else {
            return AnswerResult.CORRECT;
        }
    }

    @Override
    public Answer getBestAnswer() {
        return this.correctAnswer;
    }

}
