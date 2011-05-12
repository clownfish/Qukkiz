package de.xzise.qukkiz.reward;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.wrappers.economy.EconomyHandler;

public class CoinsReward implements Reward<CoinsRewardSettings> {

    private int start;
    private int decrease;
    private EconomyHandler economyHandler;

    public CoinsReward(CoinsRewardSettings settings) {
        this.setSettings(settings);
    }

    public void setEconomyHandler(EconomyHandler handler) {
        this.economyHandler = handler;
    }

    @Override
    public void reward(Answer answer) {
        if (this.economyHandler != null && this.economyHandler.isActive()) {
            int rewarded = this.start - answer.hint * this.decrease;
            this.economyHandler.pay(answer.player, -rewarded);
            answer.player.sendMessage(ChatColor.WHITE + "You awarded " + ChatColor.GREEN + this.economyHandler.format(rewarded) + ChatColor.WHITE + ".");
        } else {
            answer.player.sendMessage(ChatColor.RED + "You should have rewarded coins, but no economy there.");
            // TODO: Tell logger?
        }
    }

    @Override
    public void setSettings(CoinsRewardSettings settings) {
        this.start = settings.start;
        this.decrease = settings.decrease;
    }

}
