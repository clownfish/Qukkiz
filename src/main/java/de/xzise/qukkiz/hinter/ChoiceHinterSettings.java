package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

import de.xzise.ConfigurationNodeWrapper;

public class ChoiceHinterSettings extends HinterSettings {

    private final static int DEFAULT_MINIMUM_CHOICES = 2;
    
    public int minimum = DEFAULT_MINIMUM_CHOICES;
    
    public ChoiceHinterSettings(ConfigurationNode node) {
        super("choice", node);
    }

    @Override
    protected void setValues(ConfigurationNodeWrapper node) {
        this.minimum = node.getInteger("minimum", DEFAULT_MINIMUM_CHOICES);
    }

}
