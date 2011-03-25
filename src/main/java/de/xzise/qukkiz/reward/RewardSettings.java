package de.xzise.qukkiz.reward;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.util.config.ConfigurationNode;

public abstract class RewardSettings {

    private final String name;
    
    protected RewardSettings(String name) {
        this.name = name;
    }
    
    protected abstract void setValues(ConfigurationNode node);
    
    public static <T extends RewardSettings> T create(T settings, ConfigurationNode node) {
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
    
    public static <T extends RewardSettings> T create(Class<T> clazz, ConfigurationNode node) {
        try {
            return RewardSettings.create(clazz.newInstance(), node);
        } catch (InstantiationException e) {
            Trivia.logger.severe("Unable to create a reward settings class.", e);
            return null;
        } catch (IllegalAccessException e) {
            Trivia.logger.severe("Unable to create a reward settings class.", e);
            return null;
        }
    }
    
}
