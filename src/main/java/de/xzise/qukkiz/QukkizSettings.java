package de.xzise.qukkiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import nl.blaatz0r.Trivia.Trivia;

import de.xzise.qukkiz.hinter.ChoiceHinterSettings;
import de.xzise.qukkiz.hinter.NumberHinterSettings;
import de.xzise.qukkiz.hinter.ListHinterSettings;
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

    public NumberHinterSettings intHinter;
    public WordHinterSettings wordHinter;
    public ChoiceHinterSettings choiceHinter;
    public ListHinterSettings listHinter;
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
        YamlConfiguration config = new YamlConfiguration();
        if (!file.exists()) {
            //Create default file
            config.set("questions.files", "'questions/'");
            config.set("questions.delay", 5);
            config.set("questions.reveal", true);
            config.set("questions.hints.delay", 15);
            config.set("questions.hints.count", 3);
            config.set("questions.vote.ratio", 0.5);
            config.set("questions.answer.mode", "both");
            config.set("ranking.database", "qukkiz.db");
            config.set("start.onEnable", true);
            config.set("start.optIn", true);
            config.set("economy.plugin", "");
            config.set("economy.base", "");
            config.set("permissions.plugin", "");
            try {
                config.save(file);
            } catch (IOException e) {
                Trivia.logger.warning("Unable to save configuration file", e);
            }
        } else {
            try {
                config.load(file);
            } catch (FileNotFoundException e) {
                Trivia.logger.warning("File not found: " + file.getAbsolutePath());
            } catch (IOException e) {
                Trivia.logger.warning("Unable to load configuration file", e);
            } catch (InvalidConfigurationException e) {
                Trivia.logger.warning("Invalid configuration found", e);
            }
        }

        ConfigurationSection section = config.getConfigurationSection("rewards");
        this.pointsReward = RewardSettings.create(new PointsRewardSettings(), section);
        this.itemsReward = RewardSettings.create(new ItemsRewardSettings(), section);
        this.coinsReward = RewardSettings.create(new CoinsRewardSettings(), section);

        this.questionfiles = convert(config.getStringList("questions.files"), dataPath);
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
        ConfigurationSection hintsSection = config.getConfigurationSection("questions.hints");
        if (hintsSection != null) {
            this.hintCount = hintsSection.getInt("count", 3);
            this.hintDelay = hintsSection.getInt("delay", 15);

            this.intHinter = new NumberHinterSettings(hintsSection);
            this.wordHinter = new WordHinterSettings(hintsSection);
            this.choiceHinter = new ChoiceHinterSettings(hintsSection);
            this.listHinter = new ListHinterSettings(hintsSection);
        }
        
        this.optInEnabled = config.getBoolean("start.optIn", true);
        this.startOnEnable = config.getBoolean("start.onEnable", true);
        
        this.economyBaseName = config.getString("economy.base", "");
        this.economyPluginName = config.getString("economy.plugin", "");
        this.permissionsPluginName = config.getString("permissions.plugin", "");
        
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
