package de.xzise.qukkiz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.xzise.qukkiz.hinter.ChoiceHinterSettings;
import de.xzise.qukkiz.hinter.IntHinterSettings;
import de.xzise.qukkiz.hinter.WordHinterSettings;
import de.xzise.qukkiz.reward.CoinsRewardSettings;
import de.xzise.qukkiz.reward.ItemsRewardSettings;
import de.xzise.qukkiz.reward.PointsRewardSettings;
import de.xzise.qukkiz.reward.RewardSettings;

public class QukkizSettings {
    
    public CoinsRewardSettings coinsReward;
    public ItemsRewardSettings itemsReward;
    public PointsRewardSettings pointsReward;
    
    public File[] questionfiles;
    public int questionsDelay;
    
    public IntHinterSettings intHinter;
    public WordHinterSettings wordHinter;
    public ChoiceHinterSettings choiceHinter;
    public int hintCount;
    public int hintDelay;
    
    public File database;
    
    public QukkizSettings(File dataPath) {
        this.loadSettings(dataPath);
    }
    
    public void loadSettings(File dataPath) {
        Configuration config = new Configuration(new File(dataPath, "qukkiz.yml"));
        config.load();

        ConfigurationNode rewardsNode = config.getNode("rewards");
        this.pointsReward = RewardSettings.create(new PointsRewardSettings(), rewardsNode);
        this.itemsReward = RewardSettings.create(new ItemsRewardSettings(), rewardsNode);
        this.coinsReward = RewardSettings.create(new CoinsRewardSettings(), rewardsNode);
        
        this.questionfiles = convert(config.getStringList("questions.files", new ArrayList<String>(0)), dataPath);
        this.questionsDelay = config.getInt("questions.delay", 5);
        
        ConfigurationNode hintsNode = config.getNode("questions.hints");
        if (hintsNode != null) {
            this.hintCount = hintsNode.getInt("count", 3);
            this.hintDelay = hintsNode.getInt("delay", 15);
        } else {
            this.hintCount = 3;
            this.hintDelay = 15;
        }
        
        this.intHinter = new IntHinterSettings(hintsNode);
        this.wordHinter = new WordHinterSettings(hintsNode);
        this.choiceHinter = new ChoiceHinterSettings(hintsNode);
        
        this.database = new File(dataPath, config.getString("ranking.database", "qukkiz.db"));
    }
    
    private static File[] convert(List<String> list, File dataPath) {
        File[] result = new File[list.size()];
        int i = 0;
        for (String string : list) {
            result[i++] = new File(dataPath, string);
        }
        return result;
    }

}
