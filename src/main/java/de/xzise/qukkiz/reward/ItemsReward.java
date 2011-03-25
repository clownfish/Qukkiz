package de.xzise.qukkiz.reward;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.xzise.MinecraftUtil;

public class ItemsReward implements Reward<ItemsRewardSettings> {
    
    private List<Integer> items;
    
    public ItemsReward(ItemsRewardSettings settings) {
        this.setSettings(settings);
    }
    
    public ItemsReward(List<Integer> items) {
        this.items = items;
    }

    @Override
    public void reward(Player player, int hints) {
        ItemStack stack = new ItemStack(MinecraftUtil.getRandom(this.items), 1);
        if (player.getInventory().addItem(stack).isEmpty()) {
            player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item!");
        } else {
            player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item, but " + ChatColor.GREEN + "no empty" + ChatColor.WHITE + " slot!");
        }
    }

    @Override
    public void setSettings(ItemsRewardSettings settings) {
        this.items = settings.items;
    }

}
