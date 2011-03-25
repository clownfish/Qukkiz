package de.xzise.qukkiz.reward;

import org.bukkit.entity.Player;

public interface Reward<Settings extends RewardSettings> {

    void reward(Player player, int hints);
    
    void setSettings(Settings settings);
    
}
