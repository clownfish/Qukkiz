package de.xzise.qukkiz.hinter;

public interface Hinter<Settings extends HinterSettings> {

    void nextHint();
    
    String getHint();
    
    void setSettings(Settings settings);
    
}
