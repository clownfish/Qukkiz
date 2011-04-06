package de.xzise.qukkiz.hinter;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public abstract class HinterSettings {

    private final String name;
    
    protected HinterSettings(String name, ConfigurationNode node) {
        this.name = name;
        if (node == null) {
            this.setValues(Configuration.getEmptyNode());
        } else {
            this.setValues(node);
        }
    }
    
    public abstract void setValues(ConfigurationNode node);
    
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
