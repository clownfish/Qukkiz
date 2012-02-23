package de.xzise.qukkiz.reward;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import de.xzise.MinecraftUtil;
import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.reward.ItemsRewardSettings.ItemData;

public class ItemsReward extends DefaultReward<ItemsRewardSettings> {

    public ItemsReward(ItemsRewardSettings settings) {
        super(settings);
    }

    @Override
    public void reward(Answer answer) {
        ItemData data = MinecraftUtil.getRandom(this.getSettings().items);
        if (data != null) {
            ItemStack stack = data.create(1);
            if (answer.player.getInventory().addItem(stack).isEmpty()) {
                answer.player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item!");
            } else {
                answer.player.sendMessage(ChatColor.WHITE + "You have been awarded a " + ChatColor.GREEN + "random" + ChatColor.WHITE + " item, but " + ChatColor.GREEN + "no empty" + ChatColor.WHITE + " slot!");
            }
        }
    }

}
