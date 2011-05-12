package de.xzise.qukkiz.questioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
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
        List<Answer> possibleAnswers = new ArrayList<Answer>(this.answers.size());
        int bestDelta = 0;
        for (Answer answer : this.answers.values()) {
            Integer delta = this.getQuestion().testAnswer(answer.answer);
            // Answer has to be at least valid
            if (delta != null && delta != Integer.MAX_VALUE && delta != Integer.MIN_VALUE) {
                if (possibleAnswers.size() == 0) {
                    possibleAnswers.add(answer);
                    bestDelta = Math.abs(delta);
                } else {
                    delta = Math.abs(delta);
                    // All answers should have the same delta & time
                    Answer reference = possibleAnswers.get(0);
                    if (delta < bestDelta || (delta == bestDelta && answer.time < reference.time)) {
                        possibleAnswers.clear();
                        possibleAnswers.add(answer);
                        bestDelta = delta;
                    } else if (delta == bestDelta && answer.time == reference.time) {
                        possibleAnswers.add(answer);
                    }
                }
            }
        }
        return MinecraftUtil.getRandom(possibleAnswers);
    }

}
