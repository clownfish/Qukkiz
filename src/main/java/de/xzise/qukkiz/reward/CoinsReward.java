package de.xzise.qukkiz.reward;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.hinter.Answer;
import de.xzise.wrappers.economy.EconomyHandler;

public class CoinsReward extends DefaultReward<CoinsRewardSettings> {

    private EconomyHandler economyHandler;

    public CoinsReward(CoinsRewardSettings settings) {
        super(settings);
    }

    public void setEconomyHandler(EconomyHandler handler) {
        this.economyHandler = handler;
    }

    @Override
    public void reward(Answer answer) {
        if (this.economyHandler != null && this.economyHandler.isActive()) {
            final int rewarded = (int) this.getSettings().getPositiveValue(answer.hint);
            this.economyHandler.pay(answer.player, -rewarded);
            answer.player.sendMessage(ChatColor.WHITE + "You awarded " + ChatColor.GREEN + this.economyHandler.format(rewarded) + ChatColor.WHITE + ".");
        } else {
            answer.player.sendMessage(ChatColor.RED + "You should have rewarded coins, but no economy there.");
            // TODO: Tell logger?
        }
    }

}
