package de.xzise.qukkiz.hinter;

import java.text.DecimalFormat;
import java.util.Random;

public class NumberHinter extends DefaultHinter<NumberHinterSettings> {

    private final double value;
    private final DecimalFormat format;

    private double range;
    private double lastRange;
    private double lastValue;

    public NumberHinter(final double value, final NumberHinterSettings settings, final DecimalFormat format) {
        super(settings);
        this.value = value;
        this.range = this.getSettings().start;
        this.format = format;
        this.getHintValue();
    }

    private void getHintValue() {
        int range = (int) Math.ceil(this.value * this.range);
        this.lastValue = this.value + new Random().nextInt(range) - range / 2;
        this.lastRange = this.range / 2;
        this.range = this.range - this.getSettings().decrease;

    }

    @Override
    public void nextHint() {
        this.getHintValue();
    }

    @Override
    public String getHint() {
        return this.format.format(this.lastValue) + " +/-" + (int) Math.round(this.lastRange * 100) + "%";
    }

}