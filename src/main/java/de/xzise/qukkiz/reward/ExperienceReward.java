package de.xzise.qukkiz.reward;

import org.bukkit.ChatColor;

import de.xzise.MinecraftUtil;
import de.xzise.qukkiz.hinter.Answer;

public class ExperienceReward extends DefaultReward<ExperienceRewardSettings> {

    public ExperienceReward(ExperienceRewardSettings settings) {
        super(settings);
    }

    @Override
    public void reward(Answer answer) {
        double value = this.getSettings().getPositiveValue(answer.hint);
        answer.player.setExp((float) (answer.player.getExp() + value));
        answer.player.sendMessage(ChatColor.WHITE + "You awarded " + ChatColor.GREEN + MinecraftUtil.MAX_TWO_DECIMALS_FORMAT.format(value * 100) + ChatColor.WHITE + " % experience.");
    }

}
