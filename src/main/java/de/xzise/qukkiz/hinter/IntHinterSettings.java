package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

public class IntHinterSettings extends HinterSettings {

    private static final double DEFAULT_START = 0.2;
    private static final double DEFAULT_DECREASE = 0.05;
    
    public double start;
    public double decrease;
    
    public IntHinterSettings(ConfigurationNode node) {
        super("int", node);
        this.start = DEFAULT_START;
        this.decrease = DEFAULT_DECREASE;
    }
    
    @Override
    public void setValues(ConfigurationNode node) {
        this.start = node.getDouble("start", DEFAULT_START);
        this.decrease = node.getDouble("decrease", DEFAULT_DECREASE);
    }

}
