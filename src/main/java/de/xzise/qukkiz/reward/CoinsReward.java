package de.xzise.qukkiz.reward;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Bank;

import de.xzise.qukkiz.hinter.Answer;

public class CoinsReward implements Reward<CoinsRewardSettings> {

    private int start;
    private int decrease;
    private Bank bank;

    public CoinsReward(CoinsRewardSettings settings) {
        this.setSettings(settings);
    }

    public void setEconomy(Plugin plugin) {

        if (plugin instanceof iConomy) {
            this.bank = iConomy.getBank();
        } else {
            this.bank = null;
        }

    }

    @Override
    public void reward(Answer answer) {
        if (bank != null) {
            int rewarded = this.start - answer.hint * this.decrease;
            this.bank.getAccount(answer.player.getName()).add(rewarded);
            answer.player.sendMessage(ChatColor.WHITE + "You awarded " + ChatColor.GREEN + rewarded + ChatColor.WHITE + " coins.");
        } else {
            answer.player.sendMessage(ChatColor.RED + "You should have rewarded coins, but no iConomy there.");
            // TODO: Tell logger
        }
    }

    @Override
    public void setSettings(CoinsRewardSettings settings) {
        this.start = settings.start;
        this.decrease = settings.decrease;
    }

}
