package de.xzise.qukkiz.reward;

import org.bukkit.util.config.ConfigurationNode;

public class PointsRewardSettings extends RewardSettings {

    public int start;
    public int decrease;
    
    public PointsRewardSettings() {
        super("points");
    }

    @Override
    protected void setValues(ConfigurationNode node) {
        this.start = node.getInt("start", 10);
        this.decrease = node.getInt("decrease", 2);
    }
    
}
