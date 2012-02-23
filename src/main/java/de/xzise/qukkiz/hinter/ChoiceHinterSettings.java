package de.xzise.qukkiz.hinter;

import org.bukkit.configuration.ConfigurationSection;

public class ChoiceHinterSettings extends HinterSettings {

    private final static int DEFAULT_MINIMUM_CHOICES = 2;

    public int minimum = DEFAULT_MINIMUM_CHOICES;

    public ChoiceHinterSettings(ConfigurationSection node) {
        super("choice", node);
    }

    @Override
    public void setValues(ConfigurationSection node) {
        this.minimum = node.getInt("minimum", DEFAULT_MINIMUM_CHOICES);
    }

}
