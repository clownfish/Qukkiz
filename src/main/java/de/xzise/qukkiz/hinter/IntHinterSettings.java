package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

public class IntHinterSettings extends HinterSettings {

    private static final int DEFAULT_START = 200;
    private static final int DEFAULT_DECREASE = 4;
    
    public int start;
    public int decrease;
    
    public IntHinterSettings(ConfigurationNode node) {
        super("int", node);
        this.start = DEFAULT_START;
        this.decrease = DEFAULT_DECREASE;
    }
    
    @Override
    public void setValues(ConfigurationNode node) {
        this.start = node.getInt("start", DEFAULT_START);
        this.decrease = node.getInt("decrease", DEFAULT_DECREASE);
    }

}
