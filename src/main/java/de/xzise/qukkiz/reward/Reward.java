package de.xzise.qukkiz.reward;

import de.xzise.qukkiz.hinter.Answer;

public interface Reward<Settings extends RewardSettings> {

    void reward(Answer answer);
    
    void setSettings(Settings settings);
    
}
