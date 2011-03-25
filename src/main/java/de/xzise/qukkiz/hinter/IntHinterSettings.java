package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

import de.xzise.ConfigurationNodeWrapper;

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
    protected void setValues(ConfigurationNodeWrapper node) {
        this.start = node.getInteger("start", DEFAULT_START);
        this.decrease = node.getInteger("decrease", DEFAULT_DECREASE);
    }

}
