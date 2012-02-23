package de.xzise.qukkiz.hinter;

import org.bukkit.configuration.ConfigurationSection;

public class ListHinterSettings extends HinterSettings {

    private static final int DEFAULT_WORDS_PER_HINT = 1;
    private static final int DEFAULT_MINIMUM_MASKED = 2;
    private static final boolean DEFAULT_DYNAMIC_HINTS = false;

    public int wordsPerHint = DEFAULT_WORDS_PER_HINT;
    public int minimumMasked = DEFAULT_MINIMUM_MASKED;
    public boolean dynamicHints = DEFAULT_DYNAMIC_HINTS;

    public ListHinterSettings(ConfigurationSection node) {
        super("list", node);
    }

    @Override
    public void setValues(ConfigurationSection node) {
        this.wordsPerHint = node.getInt("words-per-hint", DEFAULT_WORDS_PER_HINT);
        this.minimumMasked = Math.max(node.getInt("minimum-masked", DEFAULT_MINIMUM_MASKED), 0);
        this.dynamicHints = node.getBoolean("dynamic-hints", DEFAULT_DYNAMIC_HINTS);
    }
}
