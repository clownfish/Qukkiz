package de.xzise.qukkiz.questioner;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.questions.QuestionInterface;

public class BestGuessQuestioner implements Questioner {

    private final Map<Player, Answer> answers = new HashMap<Player, Answer>();
    private final Hinter<?> hinter;
    private final QuestionInterface question;
    private final boolean cancelIfCorrect;
    
    public BestGuessQuestioner(Hinter<?> hinter, QuestionInterface question, boolean cancelIfCorrect) {
        super();
        this.hinter = hinter;
        this.question = question;
        this.cancelIfCorrect = cancelIfCorrect;
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
        Integer delta = this.getQuestion().testAnswer(answer.answer);
        if (delta != null) {
            this.answers.put(answer.player, answer);
            if (delta == 0 && this.cancelIfCorrect) {
                return AnswerResult.CORRECT;
            }
            return AnswerResult.VALID;
        } else {
            return AnswerResult.INVALID;
        }
    }

    @Override
    public Answer getBestAnswer() {
        System.out.println("get best #2");
        for (Answer answer : this.answers.values()) {
            System.out.println("Answer: " + answer.answer + " by " + answer.player + " after " + answer.time + " ms and " + answer.hint + " hints");
        }
        
        
        Answer bestAnswer = null;
        Integer bestDelta = null;
        for (Answer answer : this.answers.values()) {
            Integer delta = this.getQuestion().testAnswer(answer.answer);
            if (delta != null && delta != Integer.MAX_VALUE && delta != Integer.MIN_VALUE && (bestDelta == null || Math.abs(delta) < Math.abs(bestDelta)) && (bestAnswer == null || bestAnswer.time > answer.time)) {
                bestAnswer = answer;
                bestDelta = delta;
            }
        }
        return bestAnswer;
    }

}
