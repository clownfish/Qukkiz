package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

public class ChoiceHinterSettings extends HinterSettings {

    private final static int DEFAULT_MINIMUM_CHOICES = 2;
    
    public int minimum = DEFAULT_MINIMUM_CHOICES;
    
    public ChoiceHinterSettings(ConfigurationNode node) {
        super("choice", node);
    }

    @Override
    public void setValues(ConfigurationNode node) {
        this.minimum = node.getInt("minimum", DEFAULT_MINIMUM_CHOICES);
    }

}
