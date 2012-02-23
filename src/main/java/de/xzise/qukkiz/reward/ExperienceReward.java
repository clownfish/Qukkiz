package de.xzise.qukkiz.reward;

import de.xzise.qukkiz.hinter.Answer;

public class ExperienceReward extends DefaultReward<ExperienceRewardSettings> {

    public ExperienceReward(ExperienceRewardSettings settings) {
        super(settings);
    }

    @Override
    public void reward(Answer answer) {
        answer.player.setExp((float) (answer.player.getExp() + this.getSettings().getPositiveValue(answer.hint)));
    }

}
