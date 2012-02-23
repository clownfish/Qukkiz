package de.xzise.qukkiz.reward;

import org.bukkit.configuration.ConfigurationSection;

public class NumberRewardSettings extends RewardSettings {

    private final double initialStart;
    private final double initialDecrease;

    public double start;
    public double decrease;

    public NumberRewardSettings(final String name, final double start, final double decrease) {
        super(name);
        this.initialStart = start;
        this.initialDecrease = decrease;
        this.start = start;
        this.decrease = decrease;
    }

    @Override
    protected void setValues(ConfigurationSection node) {
        this.start = node.getDouble("start", this.initialStart);
        this.decrease = node.getDouble("decrease", this.initialDecrease);
    }

    public double getPositiveValue(final int hints) {
        return Math.max(0, this.getValue(hints));
    }

    public int getIntPositiveValue(final int hints) {
        return (int) Math.round(this.getPositiveValue(hints));
    }

    public double getValue(final int hints) {
        return this.start - this.decrease * hints;
    }

    public int getIntValue(final int hints) {
        return (int) Math.round(this.getValue(hints));
    }
}
