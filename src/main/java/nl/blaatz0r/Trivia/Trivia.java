package nl.blaatz0r.Trivia;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.NullaryCommandSender;
import de.xzise.XLogger;
import de.xzise.qukkiz.PermissionTypes;
import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.QukkizSettings.AnswerMode;
import de.xzise.qukkiz.QukkizUsers;
import de.xzise.qukkiz.commands.CommandMap;
import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.parser.QuestionParser;
import de.xzise.qukkiz.parser.TriviaParser;
import de.xzise.qukkiz.questioner.Questioner;
import de.xzise.qukkiz.questions.QuestionInterface;
import de.xzise.qukkiz.reward.CoinsReward;
import de.xzise.qukkiz.reward.ItemsReward;
import de.xzise.qukkiz.reward.PointsReward;
import de.xzise.qukkiz.reward.Reward;
import de.xzise.qukkiz.reward.RewardSettings;
import de.xzise.wrappers.WrapperServerListener;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.wrappers.permissions.PermissionsHandler;

/**
 * Trivia for Bukkit Trivia is quiz game where players can answer questions by
 * typing in the answer to a question. The player receives points for each
 * correct answer and/or optionally a random item. Every 15 seconds or so a hint
 * is given to make it a bit easier, but the answer will be worth less points
 * for each hint!
 * 
 * @author blaatz0r
 * @author Fabian Neundorf
 */
public class Trivia extends JavaPlugin {

    public static final String[] grats = { "Nice! ", "Congratulations! ", "Woop! ", "Bingo! ", "Zing! ", "Huzzah! ", "Grats! ", "Who's the man?! ", "YEAHH! ", "Well done! " };

    public String name;
    public String version;

    private Database db;
    public long startTime;
    public int hints;
    public boolean canAnswer;
    public List<CommandSender> voted;
    private boolean triviaRunning;

    // Qukkiz additions
    private List<QuestionInterface> questions = new ArrayList<QuestionInterface>();
    private Questioner questioner;
    private CommandMap commands;
    private QukkizUsers users;
    private List<Reward<? extends RewardSettings>> rewards;
    private QukkizSettings settings;
    private QuestionParser questionParser;
    private Map<Player, Answer> answers = new HashMap<Player, Answer>();

    private CoinsReward coinReward;
    private EconomyHandler economyHandler;
    private PermissionsHandler permissionsHandler;

    public static PermissionsHandler wrapper;
    public static XLogger logger;

    private boolean enableCanceled;

    // DEFAULT PLUGIN FUNCTIONS

    private void disable(String message) {
        this.enableCanceled = true;
        this.getServer().getLogger().severe("[Qukkiz] " + message);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public void onEnable() {
        try {
            if (MinecraftUtil.needUpdate(1, 3)) {
                this.disable("You need to update Bukkit Plugin Utilities to at least 1.3.0!");
                return;
            }
        } catch (NoSuchMethodError e) {
            this.disable("You need to update Bukkit Plugin Utilities to at least 1.3.0!");
            return;
        } catch (NoClassDefFoundError e) {
            this.disable("No Bukkit Plugin Utilities found!");
            return;
        }
        logger = new XLogger(this);

        if (!MinecraftUtil.OFFICAL) {
            logger.warning("You are using an inoffical version of Bukkit Plugin Utilities.");
        }

        MinecraftUtil.register(this.getServer().getPluginManager(), logger, PermissionTypes.values());

        this.getDataFolder().mkdir();

        this.settings = new QukkizSettings(this.getDataFolder());

        this.economyHandler = new EconomyHandler(this.getServer().getPluginManager(), this.settings.economyPluginName, this.settings.economyBaseName, logger);
        this.permissionsHandler = new PermissionsHandler(this.getServer().getPluginManager(), this.settings.permissionsPluginName, logger);
        Trivia.wrapper = this.permissionsHandler;

        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        this.commands = new CommandMap(this, this.settings);
        this.users = new QukkizUsers(new File(this.getDataFolder(), "stored-users.txt"), this.getServer());
        this.users.readFile();
        this.questionParser = new TriviaParser(this.settings, logger);

        this.db = new Database();
        this.db.connect(this.settings.database);
        this.db.init();

        // React on plugin enable/disable
        WrapperServerListener.createAndRegisterEvents(this, this.permissionsHandler, this.economyHandler);

        // Test all plugins once!
        this.permissionsHandler.load();
        this.economyHandler.load();

        // Register our events
        this.getServer().getPluginManager().registerEvents(new TriviaPlayerListener(this, this.users), this);

        if (this.settings.startOnEnable) {
            this.startTrivia();
        }
        enableCanceled = true;
    }

    public QukkizUsers getUsers() {
        return this.users;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.commands.executeCommand(sender, args);
    }

    public void startTrivia() {
        this.settings.loadSettings(this.getDataFolder());
        this.economyHandler.reloadConfig(this.settings.economyPluginName, this.settings.economyBaseName);
        this.permissionsHandler.setPluginName(this.settings.permissionsPluginName);
        this.permissionsHandler.load();
        this.users.setOptInEnable(this.settings.optInEnabled);
        this.loadQuestions();

        if (questions.size() != 0) {
            voted = new ArrayList<CommandSender>();
            startTime = new Date().getTime();
            hints = 0;
            canAnswer = true;
            triviaRunning = true;

            this.rewards = new ArrayList<Reward<? extends RewardSettings>>();
            if (this.settings.pointsReward != null) {
                this.rewards.add(new PointsReward(this.settings.pointsReward, this));
            }
            if (this.settings.itemsReward != null) {
                this.rewards.add(new ItemsReward(this.settings.itemsReward));
            }
            if (this.settings.coinsReward != null) {
                this.coinReward = new CoinsReward(this.settings.coinsReward);
                this.coinReward.setEconomyHandler(this.economyHandler);
                this.rewards.add(this.coinReward);
            }

            Trivia.logger.info("Qukkiz has started!");

            this.users.run();
            this.users.sendMessage(ChatColor.GREEN + "Qukkiz has started! \\o/");

            nextQuestion();
        } else {
            Trivia.logger.warning("No questions were loaded!");
            Trivia.logger.warning("Add some files to the qukkiz.yml");
            this.users.sendMessage(ChatColor.RED + "Qukkiz cannot start because no questions were loaded.");
        }
    }

    public void onDisable() {
        if (!enableCanceled) {
            this.stopTrivia();
        }
    }

    public void stopTrivia() {
        this.users.sendMessage(ChatColor.RED + "Qukkiz has stopped. :(");

        voted = new ArrayList<CommandSender>();
        hints = 0;
        canAnswer = false;
        triviaRunning = false;
        this.users.stop();
        this.getServer().getScheduler().cancelTasks(this);
    }

    // TRIVIA FUNCTIONS

    private void scheduleTask(Runnable task, int delay) {
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, task, delay * 20);
    }

    /**
     * Method to select and start the next question. Resets the number of hints,
     * reads a question, creates a hint and enables answering.
     */
    public void nextQuestion() {
        this.stopQuestion();
        this.scheduleTask(new Runnable() {

            @Override
            public void run() {
                Trivia.this.startQuestion();
            }
        }, this.settings.questionsDelay);
    }

    public void stopQuestion() {
        this.getServer().getScheduler().cancelTasks(this);
        this.canAnswer = false;
        if (this.voted != null) {
            this.voted.clear();
        }
        this.answers.clear();
        this.hints = 0;
    }

    private void startQuestion() {
        this.canAnswer = true;
        this.startTime = new Date().getTime();
        this.readQuestion();
        this.sendQuestion();
        this.startHintTimer(true);
    }

    private void startHintTimer(boolean stopTimers) {
        if (stopTimers) {
            this.getServer().getScheduler().cancelTasks(this);
        }
        this.scheduleTask(new Runnable() {

            @Override
            public void run() {
                Trivia.this.updateHint();
            }
        }, this.settings.hintDelay);
    }

    /**
     * Helper method to determine the number of characters to show in the next
     * hint. One character is shown for every ten characters that the answer is
     * long.
     */
    public void updateHint() {
        int hintCount = this.questioner.getQuestion().getMaximumHints();
        if (hintCount < 0) {
            hintCount = this.questioner.getHinter().getMaximumHints();
        }
        if (hintCount < 0) {
            hintCount = this.settings.hintCount;
        }
        if (this.hints >= hintCount) {
            if (this.canAnswer) {
                this.proposeWinner();
            }
        } else {
            this.getServer().getScheduler().cancelTasks(this);
            this.hints++;
            this.questioner.getHinter().nextHint();

            this.sendQuestion();

            this.startHintTimer(false);
        }
    }

    private void noAnswer() {
        String result = ChatColor.RED + "Nobody" + ChatColor.WHITE + " got it right.";
        if (this.settings.revealAnswer) {
            result += " The answer was " + ChatColor.GREEN + this.questioner.getQuestion().getAnswer() + ChatColor.WHITE + ".";
        }
        this.users.sendMessage(result);
        this.users.sendMessage("Next question in " + ChatColor.GREEN + this.settings.questionsDelay + ChatColor.WHITE + " seconds.");
        this.nextQuestion();
    }

    public void loadQuestions(CommandSender sender) {
        File[] list = this.settings.questionfiles;
        if (!MinecraftUtil.isSet(list)) {
            sender.sendMessage(ChatColor.RED + "No files were loaded!");
            Trivia.logger.severe("No files are added to load.");
        } else {
            if (this.triviaRunning) {
                this.users.sendMessage("Stop question to load questions.");
            }
            this.stopQuestion();
            this.questions.clear();
            this.loadQuestions(list, sender);
            sender.sendMessage("Loaded questions.");
            if (this.triviaRunning) {
                this.nextQuestion();
            }
        }
    }

    private void loadQuestions(File[] files, CommandSender sender) {
        for (File file : files) {
            if (file.isDirectory()) {
                this.loadQuestions(file.listFiles(), sender);
            } else {
                this.loadQuestions(file, sender);
            }
        }
    }

    public void loadQuestions() {
        this.loadQuestions(NullaryCommandSender.EMPTY_SENDER);
    }

    public void loadQuestions(File file, CommandSender sender) {
        if (file.exists() && file.isFile()) {
            this.questions.addAll(this.questionParser.getQuestions(file));
            sender.sendMessage("Loaded questions from " + file.getName());
            Trivia.logger.info("Loaded questions from " + file.getName());
        } else {
            Trivia.logger.warning("Failed to load " + file.getName());
        }
    }

    /**
     * Selects a random question and activate it.
     */
    public void readQuestion() {
        this.questioner = MinecraftUtil.getRandom(this.questions).createQuestioner();
    }

    public boolean triviaEnabled(CommandSender p) {
        return this.users.isPlaying(p);
    }

    public boolean permission(CommandSender sender, PermissionTypes defaultPermission, PermissionTypes adminPermission) {
        return (Trivia.wrapper.permission(sender, defaultPermission) && this.users.isPlaying(sender)) || Trivia.wrapper.permission(sender, adminPermission);
    }

    // MESSAGES

    private String[] getQuestionMessage() {
        if (this.canAnswer) {
            return new String[] { this.questioner.getQuestion().getQuestion(), "Hint [" + ChatColor.GREEN + this.hints + ChatColor.WHITE + "/" + ChatColor.GREEN + this.settings.hintCount + ChatColor.WHITE + "]: " + this.questioner.getHinter().getHint() };
        } else {
            return new String[0];
        }
    }

    /**
     * Formats and sends a question and hint to all players.
     */
    public void sendQuestion() {
        this.users.sendMessage(this.getQuestionMessage());
    }

    public void sendQuestion(CommandSender sender) {
        for (String line : this.getQuestionMessage()) {
            sender.sendMessage(line);
        }
    }

    public void sendTop(CommandSender p, int page) {
        Connection con = db.getConnection();
        try {
            int pageSize = MinecraftUtil.getMaximumLines(p);
            int offset = pageSize * (page - 1);
            PreparedStatement statement = con.prepareStatement("SELECT * FROM scores ORDER BY score DESC LIMIT ?,?;");
            statement.setInt(1, offset);
            statement.setInt(2, pageSize);
            ResultSet rs = statement.executeQuery();
            int i = offset + 1;
            String ownName = MinecraftUtil.getPlayerName(p);
            while (rs.next()) {
                ChatColor rankColor = ChatColor.GREEN;
                ChatColor nameColor = ChatColor.GREEN;
                if (ownName != null && ownName.equalsIgnoreCase(rs.getString("name"))) {
                    rankColor = ChatColor.BLUE;
                    nameColor = ChatColor.BLUE;
                }
                switch (i) {
                case 1:
                    rankColor = ChatColor.GOLD;
                    break;
                case 2:
                    rankColor = ChatColor.GRAY;
                    break;
                }
                String q = rankColor + MinecraftUtil.getOrdinal(i) + ChatColor.WHITE + ") " + nameColor + rs.getString("name") + ChatColor.WHITE + " with " + ChatColor.GREEN + rs.getInt("score") + ChatColor.WHITE + " points";
                p.sendMessage(q);
                i++;
            }
            if (i == offset + 1) {
                p.sendMessage("No players on this page.");
            }
        } catch (SQLException e) {
            Trivia.logger.warning("Unable to show the top", e);
        }
    }

    public void sendRanking(Player player) {
        this.sendRanking(player.getName(), player);
    }

    public void sendRanking(String name, CommandSender sender) {
        Connection con = db.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT score FROM scores WHERE name = ?;");
            ps.setString(1, name);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (!rs.isClosed()) {
                int score = rs.getInt("score");
                PreparedStatement s = con.prepareStatement("SELECT (COUNT(*)+1) AS rank FROM scores WHERE score > ?;");
                s.setInt(1, score);
                s.execute();
                String rank = MinecraftUtil.getOrdinal(s.getResultSet().getInt("rank"));
                sender.sendMessage((sender instanceof Player && ((Player) sender).getName().equals(name) ? "You are" : ChatColor.GREEN + name + ChatColor.WHITE + " is") + " currently ranked " + ChatColor.GREEN + rank + ChatColor.WHITE + " with " + ChatColor.GREEN + score + ChatColor.WHITE + " points.");
            } else {
                sender.sendMessage((sender instanceof Player && ((Player) sender).getName().equals(name) ? "You are" : ChatColor.GREEN + name + ChatColor.WHITE + " is") + " currently not ranked.");
            }

        } catch (SQLException e) {
            Trivia.logger.warning("Unable to show the rank of " + name, e);
        }

    }

    public boolean answerQuestion(String answer, Player player, boolean printWrongAnswer) {
        if (this.users.isPlaying(player) && this.canAnswer) {
            Answer answerObject = new Answer(new Date().getTime() - this.startTime, this.hints, answer, player);
            switch (this.questioner.putAnswer(answerObject)) {
            case FINISHED:
                this.proposeWinner();
                return true;
            case NOT_FINISHED:
                this.proposeAnswer(answerObject);
                return true;
            case VALID:
                player.sendMessage("Qukkiz recognized '" + ChatColor.GREEN + answer + ChatColor.WHITE + "' as your answer.");
                return false;
            case INVALID:
                if (printWrongAnswer) {
                    player.sendMessage("The answer '" + ChatColor.GREEN + answer + ChatColor.WHITE + "' is wrong. Sorry.");
                }
                return false;
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean isCommandModeAllowed() {
        return this.settings.answerMode == AnswerMode.COMMAND || this.settings.answerMode == AnswerMode.BOTH;
    }

    public boolean isChatModeAllowed() {
        return this.settings.answerMode == AnswerMode.CHAT || this.settings.answerMode == AnswerMode.BOTH;
    }

    private void proposeWinner() {
        List<Answer> a = this.questioner.getBestAnswers();
        if (MinecraftUtil.isSet(a)) {
            this.canAnswer = false;

            if (a.size() == 1) {
                double time = Math.round(a.get(0).time / 10) / 100.0;

                this.users.sendMessage(ChatColor.DARK_GREEN + MinecraftUtil.getRandom(grats) + ChatColor.GREEN + a.get(0).player.getDisplayName() + ChatColor.DARK_GREEN + " got the answer in " + ChatColor.GREEN + String.valueOf(time) + ChatColor.DARK_GREEN + " seconds!");
            } else {
                StringBuilder proposeBuilder = new StringBuilder(ChatColor.DARK_GREEN.toString()).append(MinecraftUtil.getRandom(grats)).append(ChatColor.GREEN + ": ");
                for (Iterator<Answer> answerIterator = a.iterator();answerIterator.hasNext();) {
                    Answer answer = answerIterator.next();
                    double time = Math.round(answer.time / 10) / 100.0;
                    proposeBuilder.append(answer.player.getDisplayName()).append(ChatColor.DARK_GREEN + " (" + ChatColor.GREEN).append(time).append(ChatColor.DARK_GREEN + "s )");
                    if (answerIterator.hasNext()) {
                        proposeBuilder.append(", " + ChatColor.GREEN);
                    }
                }
                this.users.sendMessage(proposeBuilder.toString());
            }
            this.users.sendMessage(ChatColor.DARK_GREEN + "The answer was " + ChatColor.GREEN + this.questioner.getQuestion().getAnswer());

            for (Answer answer : a) {
                this.reward(answer);
            }
            this.nextQuestion();
        } else {
            this.noAnswer();
        }
    }

    private void proposeAnswer(Answer answer) {
        this.users.sendMessage("New answer from " + ChatColor.GREEN + answer.player.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.GREEN + answer.answer, "The hint timer was reset.");
        this.startHintTimer(true);
    }

    public void reward(Answer answer) {
        for (Reward<? extends RewardSettings> reward : this.rewards) {
            reward.reward(answer);
        }
    }

    // BASIC GETTERS AND SETTERS
    public boolean isRunning() {
        return this.triviaRunning;
    }

    public Database getDb() {
        return db;
    }
}
