package de.xzise.qukkiz.reward;

import nl.blaatz0r.Trivia.Trivia;

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
            Trivia.logger.info("iConomy found and CoinsReward is enabled.");
        } else {
            this.bank = null;
            Trivia.logger.info("iConomy not found and CoinsRewards is disabled.");
        }
    }

    @Override
    public void reward(Answer answer) {
        if (bank != null) {
            int rewarded = this.start - answer.hint * this.decrease;
            this.bank.getAccount(answer.player.getName()).add(rewarded);
            answer.player.sendMessage(ChatColor.WHITE + "You awarded " + ChatColor.GREEN + this.bank.format(rewarded) + ChatColor.WHITE + ".");
        } else {
            answer.player.sendMessage(ChatColor.RED + "You should have rewarded coins, but no iConomy there.");
            // TODO: Tell logger?
        }
    }

    @Override
    public void setSettings(CoinsRewardSettings settings) {
        this.start = settings.start;
        this.decrease = settings.decrease;
    }

}
