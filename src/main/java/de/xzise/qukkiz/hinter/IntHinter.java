package de.xzise.qukkiz.hinter;

import java.util.Random;

public class IntHinter extends DefaultHinter<IntHinterSettings> {

    private final int value;
    private double range;
    private double lastRange;
    private int lastValue;
    
    public IntHinter(int value, IntHinterSettings settings) {
        super(settings);
        this.value = value;
        this.range = this.getSettings().start;
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
        return this.lastValue + " +/-" + (int) Math.round(this.lastRange * 100) + "%";
    }

}