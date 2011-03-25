package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

import de.xzise.ConfigurationNodeWrapper;

public class ChoiceHinterSettings extends HinterSettings {

    public ChoiceHinterSettings(ConfigurationNode node) {
        super("choice", node);
    }

    @Override
    protected void setValues(ConfigurationNodeWrapper node) {
        // There are no settings (yet?)
    }

}
