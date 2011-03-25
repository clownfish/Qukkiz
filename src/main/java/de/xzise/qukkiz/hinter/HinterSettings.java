package de.xzise.qukkiz.hinter;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.util.config.ConfigurationNode;

import de.xzise.ConfigurationNodeWrapper;

public abstract class HinterSettings {

    private final String name;
    
    protected HinterSettings(String name, ConfigurationNode node) {
        this.name = name;
        this.setValues(node);
    }
    
    protected abstract void setValues(ConfigurationNodeWrapper node);
    
    public final void setValues(ConfigurationNode node) {
        this.setValues(new ConfigurationNodeWrapper(node));
    }
    
    // Idea 2
    public static <T extends HinterSettings> T create(Class<T> clazz, ConfigurationNode node) {
        try {
            return HinterSettings.create(clazz.newInstance(), node);
        } catch (InstantiationException e) {
            Trivia.logger.severe("Unable to create a hinter settings class.", e);
            return null;
        } catch (IllegalAccessException e) {
            Trivia.logger.severe("Unable to create a hinter settings class.", e);
            return null;
        }
    }
    
    public static <T extends HinterSettings> T create(T settings, ConfigurationNode node) {
        if (node != null) {
            ConfigurationNode subNode = node.getNode(settings.name);
            if (subNode != null) {
                settings.setValues(node.getNode(settings.name));
                return settings;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
}
