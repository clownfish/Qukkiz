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
        double delta = this.getQuestion().testAnswer(answer.answer);
        if (delta != Double.NaN) {
            this.answers.put(answer.player, answer);
            if (MinecraftUtil.equals(delta, 0, 0.00000000000001) && this.cancelIfCorrect) {
                return AnswerResult.FINISHED;
            }
            return AnswerResult.VALID;
        } else {
            return AnswerResult.INVALID;
        }
    }

    @Override
    public List<Answer> getBestAnswers() {
        List<Answer> possibleAnswers = new ArrayList<Answer>(this.answers.size());
        double bestDelta = 0;
        for (Answer answer : this.answers.values()) {
            double delta = this.getQuestion().testAnswer(answer.answer);
            // Answer has to be at least valid
            if (delta != Double.NaN && delta != Double.NEGATIVE_INFINITY && delta != Double.POSITIVE_INFINITY) {
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
        return possibleAnswers;
    }

}
