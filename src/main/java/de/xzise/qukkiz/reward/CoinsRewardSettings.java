package de.xzise.qukkiz.reward;

import org.bukkit.util.config.ConfigurationNode;

public class CoinsRewardSettings extends RewardSettings {
    
    public int start;
    public int decrease;
    
    public CoinsRewardSettings() {
        super("coins");
    }

    @Override
    protected void setValues(ConfigurationNode node) {
        this.start = node.getInt("start", 8);
        this.decrease = node.getInt("decrease", 3);
    }

}
