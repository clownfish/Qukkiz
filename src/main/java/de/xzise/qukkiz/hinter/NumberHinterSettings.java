package de.xzise.qukkiz.hinter;

import org.bukkit.configuration.ConfigurationSection;

public class NumberHinterSettings extends HinterSettings {

    private static final double DEFAULT_START = 0.2;
    private static final double DEFAULT_DECREASE = 0.05;

    public double start;
    public double decrease;

    public NumberHinterSettings(ConfigurationSection node) {
        super("number", node);
        this.start = DEFAULT_START;
        this.decrease = DEFAULT_DECREASE;
    }

    @Override
    public void setValues(ConfigurationSection node) {
        this.start = node.getDouble("start", DEFAULT_START);
        this.decrease = node.getDouble("decrease", DEFAULT_DECREASE);
    }

}
