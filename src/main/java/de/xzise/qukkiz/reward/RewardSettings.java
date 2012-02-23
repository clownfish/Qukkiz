package de.xzise.qukkiz.reward;

import org.bukkit.configuration.ConfigurationSection;

public abstract class RewardSettings {

    private final String name;

    protected RewardSettings(String name) {
        this.name = name;
    }

    protected abstract void setValues(ConfigurationSection node);

    public static <T extends RewardSettings> T create(T settings, ConfigurationSection section) {
        if (section != null) {
            ConfigurationSection subSection = section.getConfigurationSection(settings.name);
            if (subSection != null) {
                settings.setValues(subSection);
                return settings;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
