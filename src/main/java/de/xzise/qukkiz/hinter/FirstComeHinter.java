package de.xzise.qukkiz.hinter;

public abstract class FirstComeHinter<Settings extends HinterSettings> extends DefaultHinter<Settings> {

    private Answer correctAnswer;
    
    public FirstComeHinter(Settings settings) {
        super(settings);
        this.correctAnswer = null;
    }

    @Override
    public boolean putAnswer(Answer answer) {
        if (this.correctAnswer == null && this.getQuestion().testAnswer(answer.answer)) {
            this.correctAnswer = answer;
        }
        return this.correctAnswer != null;
    }

    @Override
    public Answer getBestAnswer() {
        return this.correctAnswer;
    }

}
