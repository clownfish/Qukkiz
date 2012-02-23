package de.xzise.qukkiz.hinter;

import org.bukkit.configuration.ConfigurationSection;

public abstract class HinterSettings {

    protected HinterSettings(String name, ConfigurationSection node) {
        if (node != null) {
            ConfigurationSection subSection = node.getConfigurationSection(name);
            if (subSection != null) {
                this.setValues(subSection);
            }
        }
    }

    public abstract void setValues(ConfigurationSection node);
}
