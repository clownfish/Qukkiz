package de.xzise.qukkiz.hinter;

import org.bukkit.configuration.ConfigurationSection;

public class WordHinterSettings extends HinterSettings {

    private static final int DEFAULT_LETTERS_PER_HINT = 8;
    private static final int DEFAULT_MINIMUM_MASKED = 2;

    public int lettersPerHint = DEFAULT_LETTERS_PER_HINT;
    public int minimumMasked = DEFAULT_MINIMUM_MASKED;

    public WordHinterSettings(ConfigurationSection node) {
        super("word", node);
    }

    @Override
    public void setValues(ConfigurationSection node) {
        this.lettersPerHint = node.getInt("letters-per-hint", DEFAULT_LETTERS_PER_HINT);
        this.minimumMasked = Math.max(node.getInt("minimum-masked", DEFAULT_MINIMUM_MASKED), 0);
    }

}
