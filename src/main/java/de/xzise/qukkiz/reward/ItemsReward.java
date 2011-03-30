package de.xzise.qukkiz.reward;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import de.xzise.MinecraftUtil;
import de.xzise.qukkiz.hinter.Answer;

public class ItemsReward implements Reward<ItemsRewardSettings> {
    
    private List<Integer> items;
    
    public ItemsReward(ItemsRewardSettings settings) {
        this.setSettings(settings);
    }
    
    public ItemsReward(List<Integer> items) {
        this.items = items;
    }

    @Override
    public void reward(Answer answer) {
        ItemStack stack = new ItemStack(MinecraftUtil.getRandom(this.items), 1);
        if (answer.player.getInventory().addItem(stack).isEmpty()) {
            answer.player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item!");
        } else {
            answer.player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item, but " + ChatColor.GREEN + "no empty" + ChatColor.WHITE + " slot!");
        }
    }

    @Override
    public void setSettings(ItemsRewardSettings settings) {
        this.items = settings.items;
    }

}
