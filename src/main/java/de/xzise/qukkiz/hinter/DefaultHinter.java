package de.xzise.qukkiz.hinter;

public abstract class DefaultHinter<Settings extends HinterSettings> implements Hinter<Settings> {

    private Settings settings;
    
    public DefaultHinter(Settings settings) {
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

    @Override
    public int getMaximumHints() {
        return -1;
    }
}
