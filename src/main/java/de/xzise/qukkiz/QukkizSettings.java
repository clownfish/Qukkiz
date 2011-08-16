package de.xzise.qukkiz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.blaatz0r.Trivia.Trivia;

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

    public enum AnswerMode { COMMAND, CHAT, BOTH }

    public CoinsRewardSettings coinsReward;
    public ItemsRewardSettings itemsReward;
    public PointsRewardSettings pointsReward;

    public File[] questionfiles;
    public int questionsDelay;
    public boolean revealAnswer;
    public double voteRatio;
    public AnswerMode answerMode;

    public IntHinterSettings intHinter;
    public WordHinterSettings wordHinter;
    public ChoiceHinterSettings choiceHinter;
    public int hintCount;
    public int hintDelay;

    public File database;

    public boolean optInEnabled;
    public boolean startOnEnable;
    public String economyPluginName;
    public String economyBaseName;
    public String permissionsPluginName;

    public QukkizSettings(File dataPath) {
        this.loadSettings(dataPath);
    }
    
    public void loadSettings(File dataPath) {
        File file = new File(dataPath, "qukkiz.yml");
        Configuration config = new Configuration(file);
        if (!file.exists()) {
            //TODO: Create default file
            config.setProperty("questions.files", "'questions/'");
            config.setProperty("questions.delay", 5);
            config.setProperty("questions.reveal", true);
            config.setProperty("questions.hints.delay", 15);
            config.setProperty("questions.hints.count", 3);
            config.setProperty("questions.vote.ratio", 0.5);
            config.setProperty("questions.answer.mode", "both");
            config.setProperty("ranking.database", "qukkiz.db");
            config.setProperty("start.onEnable", true);
            config.setProperty("start.optIn", true);
            config.setProperty("economy.plugin", "");
            config.setProperty("economy.base", "");
            config.setProperty("permissions.plugin", "");
            config.save();
        } else {
            config.load();
        }

        ConfigurationNode rewardsNode = config.getNode("rewards");
        this.pointsReward = RewardSettings.create(new PointsRewardSettings(), rewardsNode);
        this.itemsReward = RewardSettings.create(new ItemsRewardSettings(), rewardsNode);
        this.coinsReward = RewardSettings.create(new CoinsRewardSettings(), rewardsNode);
        
        this.questionfiles = convert(config.getStringList("questions.files", new ArrayList<String>(0)), dataPath);
        this.questionsDelay = config.getInt("questions.delay", 5);
        this.revealAnswer = config.getBoolean("questions.reveal", true);
        this.voteRatio = config.getDouble("questions.vote.ratio", 0.5);
        String answerModeText = config.getString("questions.answer.mode", "both");
        if (answerModeText.equalsIgnoreCase("command")) {
            this.answerMode = AnswerMode.COMMAND;
        } else if (answerModeText.equalsIgnoreCase("chat")) {
            this.answerMode = AnswerMode.CHAT;
        } else {
            this.answerMode = AnswerMode.BOTH;
            if (!answerModeText.equalsIgnoreCase("both")) {
                Trivia.logger.info("Unknown answer mode set ('" + answerModeText + "'). Default to both.");
            }
        }
        
        ConfigurationNode hintsNode = getNode(config, "questions.hints");
        
        this.hintCount = hintsNode.getInt("count", 3);
        this.hintDelay = hintsNode.getInt("delay", 15);
        
        this.intHinter = new IntHinterSettings(hintsNode);
        this.wordHinter = new WordHinterSettings(hintsNode);
        this.choiceHinter = new ChoiceHinterSettings(hintsNode);
        
        this.optInEnabled = config.getBoolean("start.optIn", true);
        this.startOnEnable = config.getBoolean("start.onEnable", true);
        
        this.economyBaseName = config.getString("economy.base", "");
        this.economyPluginName = config.getString("economy.plugin", "");
        this.permissionsPluginName = config.getString("permissions.plugin", "");
        
        this.database = new File(dataPath, config.getString("ranking.database", "qukkiz.db"));
    }
    
    private static ConfigurationNode getNode(ConfigurationNode node, String nodeName) {
        ConfigurationNode subNode = node.getNode(nodeName);
        return subNode == null ? Configuration.getEmptyNode() : subNode; 
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
