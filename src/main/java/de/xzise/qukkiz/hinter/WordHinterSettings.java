package de.xzise.qukkiz.hinter;

import org.bukkit.util.config.ConfigurationNode;

import de.xzise.ConfigurationNodeWrapper;

public class WordHinterSettings extends HinterSettings {

    private static final int DEFAULT_LETTERS_PER_HINT = 8;
    private static final int DEFAULT_MINIMUM_MASKED = 2;
    
    public int lettersPerHint = DEFAULT_LETTERS_PER_HINT;
    public int minimumMasked = DEFAULT_MINIMUM_MASKED;
    
    public WordHinterSettings(ConfigurationNode node) {
        super("word", node);
    }

    @Override
    protected void setValues(ConfigurationNodeWrapper node) {
        this.lettersPerHint = node.getInteger("letters-per-hint", DEFAULT_LETTERS_PER_HINT);
        this.minimumMasked = Math.max(node.getInteger("minimum-masked", DEFAULT_MINIMUM_MASKED), 0);
    }

}
