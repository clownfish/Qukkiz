package de.xzise.qukkiz.reward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.ConfigurationNode;

public class ItemsRewardSettings extends RewardSettings {
    
    public List<Integer> items;
    
    public ItemsRewardSettings() {
        super("items");
    }

    @Override
    protected void setValues(ConfigurationNode node) {
        this.items = node.getIntList("list", new ArrayList<Integer>(0));
    }
}
