package de.xzise.qukkiz.reward;

public abstract class DefaultReward<Settings extends RewardSettings> implements Reward<Settings> {

    private Settings settings;
    
    public DefaultReward(Settings settings) {
        super();
        this.setSettings(settings);
    }
    
    protected Settings getSettings() {
        return this.settings;
    }
    
    @Override
    public final void setSettings(Settings settings) {
        this.settings = settings;
    }
    
}
