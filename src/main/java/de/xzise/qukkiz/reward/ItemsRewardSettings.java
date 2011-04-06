package de.xzise.qukkiz.reward;

import java.util.ArrayList;
import java.util.List;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import de.xzise.MinecraftUtil;

public class ItemsRewardSettings extends RewardSettings {

    public class ItemData {
        public final Material material;
        public final short damage;
        public final Byte meta;

        public ItemData(Material material, short damage, Byte meta) {
            this.material = material;
            this.damage = damage;
            this.meta = meta;
        }

        public ItemStack create(int amount) {
            return new ItemStack(this.material, amount, this.damage, this.meta);
        }
    }

    public List<ItemData> items;

    public ItemsRewardSettings() {
        super("items");
    }

    @Override
    protected void setValues(ConfigurationNode node) {
        List<String> items = node.getStringList("list", new ArrayList<String>(0));
        this.items = new ArrayList<ItemsRewardSettings.ItemData>(items.size());
        for (String string : items) {
            String materialIdString = null;
            short d = 0;
            Byte metaData = null;
            if (string.matches("\\d+d\\d?\\.\\d+")) {
                String[] data = string.split("d");
                if (data.length == 2) {
                    materialIdString = data[0];
                    Short damage = MinecraftUtil.tryAndGetShort(data[1]);
                    if (damage == null) {
                        Trivia.logger.warning("The damage value is not a valid short. (" + string + ")");
                        continue;
                    }
                    d = damage;
                } else {
                    Trivia.logger.warning("The item line is invalid. (" + string + ")");
                    continue;
                }
            } else if (string.matches("\\d+m\\d+")) {
                String[] data = string.split("m");
                if (data.length == 2) {
                    materialIdString = data[0];
                    Byte meta = MinecraftUtil.tryAndGetByte(data[1]);
                    if (meta == null) {
                        Trivia.logger.warning("The data value is not a valid byte. (" + string + ")");
                        continue;
                    }
                    metaData = meta;
                } else {
                    Trivia.logger.warning("The item line is invalid. (" + string + ")");
                    continue;
                }
            } else if (string.matches("\\d+")) {
                materialIdString = string;
            }
            if (materialIdString != null) {
                Integer materialId = MinecraftUtil.tryAndGetInteger(materialIdString);
                if (materialId == null) {
                    Trivia.logger.warning("The material value is not a valid integer. (" + string + ")");
                    continue;
                }
                Material material = Material.getMaterial(materialId);
                if (material == null) {
                    Trivia.logger.warning("The material id is not a valid material. (" + string + ")");
                    continue;
                }
                this.items.add(new ItemData(material, d, metaData));
            } else {
                Trivia.logger.warning("The item line is invalid. (" + string + ")");
            }
        }
    }
}
