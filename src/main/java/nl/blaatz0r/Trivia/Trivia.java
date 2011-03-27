package nl.blaatz0r.Trivia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.qukkiz.PermissionWrapper;
import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.QukkizUsers;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;
import de.xzise.qukkiz.commands.CommandMap;
import de.xzise.qukkiz.hinter.Hinter;
import de.xzise.qukkiz.hinter.HinterSettings;
import de.xzise.qukkiz.questions.EstimateQuestion;
import de.xzise.qukkiz.questions.MultipleChoiceQuestion;
import de.xzise.qukkiz.questions.QuestionInterface;
import de.xzise.qukkiz.questions.ScrambleQuestion;
import de.xzise.qukkiz.questions.TextQuestion;
import de.xzise.qukkiz.reward.CoinsReward;
import de.xzise.qukkiz.reward.ItemsReward;
import de.xzise.qukkiz.reward.PointsReward;
import de.xzise.qukkiz.reward.Reward;
import de.xzise.qukkiz.reward.RewardSettings;

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
    private List<QuestionInterface> questions;
    private Hinter<? extends HinterSettings> hinter;
    private CommandMap commands;
    private QukkizUsers users;
    private List<Reward<? extends RewardSettings>> rewards;
    private QukkizSettings settings;
    private Timer timer;
    private TimerTask task;

    private CoinsReward coinReward;

    public static PermissionWrapper wrapper = new PermissionWrapper();
    public static XLogger logger;

    // DEFAULT PLUGIN FUNCTIONS

    public void onEnable() {
        try {
            logger = new XLogger(this);
        } catch (NoSuchMethodError nsme) {
            logger = new XLogger("Minecraft", "Trivia");
            logger.warning("Using old constructor!");
        }

        this.getDataFolder().mkdir();

        this.settings = new QukkizSettings(this.getDataFolder());
        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        this.commands = new CommandMap(this);
        this.users = new QukkizUsers(new File(this.getDataFolder(), "stored-users.txt"));
        this.users.readFile(this.getServer());

        this.db = new Database();
        db.connect(this.settings.database);
        db.init();

        // Read trivia question files (only once)
        this.questions = new ArrayList<QuestionInterface>();
        this.loadQuestions();

        // React on plugin enable/disable
        ServerListener serverListener = new ServerListener() {
            @Override
            public void onPluginEnabled(PluginEvent event) {
                String name = event.getPlugin().getDescription().getName();
                if (name.equals("Permissions")) {
                    Trivia.wrapper.init(event.getPlugin());
                } else if (name.equals("iConomy")) {
                    if (Trivia.this.coinReward != null) {
                        Trivia.this.coinReward.setEconomy(event.getPlugin());
                    }
                }
            }

            @Override
            public void onPluginDisabled(PluginEvent event) {
                String name = event.getPlugin().getDescription().getName();
                if (name.equals("Permissions")) {
                    Trivia.wrapper.init(null);
                } else if (name.equals("iConomy")) {
                    if (Trivia.this.coinReward != null) {
                        Trivia.this.coinReward.setEconomy(null);
                    }
                }
            }
        };

        // Test all plugins once!
        Trivia.wrapper.init(this.getServer().getPluginManager().getPlugin("Permissions"));
        if (this.coinReward != null) {
            this.coinReward.setEconomy(this.getServer().getPluginManager().getPlugin("iConomy"));
        }

        PlayerListener playerListener = new TriviaPlayerListener(this, this.users);

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Low, this);

        this.startTrivia();
        logger.info(name + " " + version + " enabled");
    }

    public QukkizUsers getUsers() {
        return this.users;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.commands.executeCommand(sender, args);
    }

    public void startTrivia() {
        this.settings.loadSettings(this.getDataFolder());
        this.loadQuestions();

        if (questions.size() != 0) {
            // Start a new timer
            if (this.timer != null) {
                this.timer.cancel();
            }
            this.timer = new Timer();

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
                this.rewards.add(new CoinsReward(this.settings.coinsReward));
            }

            Trivia.logger.info("Trivia has started!");
            this.users.sendMessage(ChatColor.GREEN + "Trivia has started! \\o/");

            nextQuestion();
        } else {
            Trivia.logger.warning("No questions were loaded!");
            Trivia.logger.warning("Add some files to the qukkiz.yml");
            this.users.sendMessage(ChatColor.RED + "Trivia cannot start because no questions were loaded.");
        }
    }

    public void onDisable() {
        this.stopTrivia();

        Trivia.logger.info(name + " " + version + " disabled");
    }

    public void stopTrivia() {
        this.users.sendMessage(ChatColor.RED + "Trivia has stopped. :(");

        voted = new ArrayList<CommandSender>();
        hints = 0;
        canAnswer = false;
        triviaRunning = false;

        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    // TRIVIA FUNCTIONS

    private void scheduleTask(TimerTask task, int delay) {
        this.task = task;
        this.timer.schedule(task, delay);
    }

    /**
     * Method to select and start the next question. Resets the number of hints,
     * reads a question, creates a hint and enables answering.
     */
    public void nextQuestion() {
        this.stopQuestion();
        this.scheduleTask(new TimerTask() {

            @Override
            public void run() {
                Trivia.this.startQuestion();
            }
        }, this.settings.questionsDelay * 1000);
    }

    public void stopQuestion() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.canAnswer = false;
        if (this.voted != null) {
            this.voted.clear();
        }
        this.hints = 0;
    }

    private void startQuestion() {
        this.canAnswer = true;
        this.startTime = new Date().getTime();
        this.readQuestion();
        this.sendQuestion();
        this.startHintTimer();
    }

    private void startHintTimer() {
        this.scheduleTask(new TimerTask() {

            @Override
            public void run() {
                Trivia.this.updateHint();
            }
        }, this.settings.hintDelay * 1000);
    }

    /**
     * Helper method to determine the number of characters to show in the next
     * hint. One character is shown for every ten characters that the answer is
     * long.
     */
    public void updateHint() {
        if (this.hints == this.settings.hintCount) {
            this.users.sendMessage(ChatColor.RED + "Nobody" + ChatColor.WHITE + " got it right. The answer was " + ChatColor.GREEN + this.hinter.getQuestion().getAnswer());
            this.users.sendMessage("Next question in " + ChatColor.GREEN + this.settings.questionsDelay + ChatColor.WHITE + " seconds.");
            this.nextQuestion();
        } else {
            if (this.task != null) {
                this.task.cancel();
            }
            this.hints++;
            this.hinter.nextHint();

            this.sendQuestion();

            this.startHintTimer();
        }
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
        this.loadQuestions(new CommandSender() {

            @Override
            public void sendMessage(String message) {
            }

            @Override
            public boolean isOp() {
                return false;
            }

            @Override
            public Server getServer() {
                return Trivia.this.getServer();
            }

        });
    }

    public void loadQuestions(File file, CommandSender sender) {
        if (file.exists() && file.isFile()) {
            List<String> q = new ArrayList<String>();
            try {
                q.addAll(this.readLines(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parseTriviaQuestions(q, this.questions);
            sender.sendMessage("Loaded questions from " + file.getName());
            Trivia.logger.info("Loaded questions from " + file.getName());
        } else {
            Trivia.logger.warning("Failed to load " + file.getName());
        }
    }

    public List<QuestionInterface> parseTriviaQuestions(List<String> triviaQuestions) {
        List<QuestionInterface> result = new ArrayList<QuestionInterface>(triviaQuestions.size());

        for (String string : triviaQuestions) {
            if (!string.isEmpty()) {
                String[] segments = string.split("\\*");
                if (segments.length > 1) {
                    if (segments[0].equalsIgnoreCase("scramble")) {
                        if (segments.length == 2) {
                            result.add(new ScrambleQuestion(segments[1], this.settings));
                        } else {
                            Trivia.logger.warning("Bad format in scramble question: " + string);
                        }
                    } else if (segments[0].equalsIgnoreCase("multiple choice")) {
                        if (segments.length > 3) {
                            result.add(MultipleChoiceQuestion.create(segments, this.settings));
                        } else {
                            Trivia.logger.warning("Bad format in multiple choice question: " + string);
                        }
                    } else if (segments[0].equalsIgnoreCase("estimate")) {
                        if (segments.length == 3) {
                            try {
                                result.add(new EstimateQuestion(segments[1], this.settings, Integer.parseInt(segments[2])));
                            } catch (NumberFormatException nfe) {
                                Trivia.logger.warning("Bad format in estimate question (unable to parse to int): " + string);
                            }
                        } else {
                            Trivia.logger.warning("Bad format in estimate question: " + string);
                        }
                    } else {
                        result.add(new TextQuestion(segments[0], this.settings, Arrays.copyOfRange(segments, 1, segments.length)));
                    }
                } else {
                    Trivia.logger.warning("Bad format in question: " + string);
                }
            }
        }

        return result;
    }

    public void parseTriviaQuestions(List<String> triviaQuestions, List<QuestionInterface> questions) {
        questions.addAll(parseTriviaQuestions(triviaQuestions));
    }

    /**
     * Reads a question from a file and updates question and answer.
     */
    public void readQuestion() {
        this.hinter = MinecraftUtil.getRandom(this.questions).createHinter();
    }

    public boolean triviaEnabled(CommandSender p) {
        return this.users.getActives().contains(p);
    }

    public List<String> readLines(File f) throws IOException {
        List<String> lines = new ArrayList<String>();
        FileReader fileReader = new FileReader(f);
        try {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            fileReader.close();
        }
        return lines;
    }

    public boolean permission(CommandSender sender, PermissionTypes defaultPermission, PermissionTypes adminPermission) {
        return (Trivia.wrapper.permission(sender, defaultPermission) && this.triviaEnabled(sender)) || Trivia.wrapper.permission(sender, adminPermission);
    }

    // MESSAGES

    private String[] getQuestionMessage() {
        if (this.canAnswer) {
            return new String[] { this.hinter.getQuestion().getQuestion(), "Hint [" + ChatColor.GREEN + this.hints + ChatColor.WHITE + "/" + ChatColor.GREEN + this.settings.hintCount + ChatColor.WHITE + "]: " + this.hinter.getHint() };
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

    public void sendTop(CommandSender p) {
        Connection con = db.getConnection();
        try {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM scores ORDER BY score DESC LIMIT 5;");

            int i = 1;
            while (rs.next()) {
                String q = ChatColor.AQUA + String.valueOf(i) + ". " + ChatColor.BLUE + rs.getString("name") + ChatColor.AQUA + " - " + ChatColor.BLUE + rs.getInt("score") + " points";
                p.sendMessage(q);
                i++;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendRanking(Player player) {
        this.sendRanking(player.getName(), player);
    }

    public void sendRanking(String name, CommandSender sender) {
        Connection con = db.getConnection();
        try {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("SELECT (COUNT(*)+1) AS rank FROM scores WHERE score > (SELECT score FROM scores WHERE name = '" + name + "' LIMIT 1);");

            int rank = rs.getInt("rank");
            ResultSet check = stat.executeQuery("SELECT * FROM scores WHERE name = '" + name + "';");
            if (check.next()) {
                String rankName = "";
                if ((rank % 100) / 10 == 1) {
                    rankName = "th";
                } else {
                    switch (rank % 10) {
                    case (1):
                        rankName = "st";
                        break;
                    case (2):
                        rankName = "nd";
                        break;
                    case (3):
                        rankName = "rd";
                        break;
                    default:
                        rankName = "th";
                    }
                }
                sender.sendMessage((sender instanceof Player && ((Player) sender).getName().equals(name) ? "You are" : ChatColor.GREEN + name + ChatColor.WHITE + " is") + " currently ranked " + ChatColor.GREEN + rank + rankName + ChatColor.WHITE + ".");
            } else {
                sender.sendMessage((sender instanceof Player && ((Player) sender).getName().equals(name) ? "You are" : ChatColor.GREEN + name + ChatColor.WHITE + " is") + " currently not ranked.");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean answerQuestion(String answer, Player player) {
        if (this.canAnswer && this.hinter.getQuestion().testAnswer(answer)) {
            this.canAnswer = false;
            long endTime = (new Date().getTime()) - this.startTime;

            double time = Math.round(endTime / 10) / 100;

            this.users.sendMessage(ChatColor.DARK_GREEN + MinecraftUtil.getRandom(grats) + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + " got the answer in " + ChatColor.GREEN + String.valueOf(time) + ChatColor.DARK_GREEN + " seconds!");
            this.users.sendMessage(ChatColor.DARK_GREEN + "The answer was " + ChatColor.GREEN + this.hinter.getQuestion().getAnswer());

            this.reward(player);
            this.nextQuestion();
            return true;
        } else {
            return false;
        }
    }

    public void reward(Player p) {
        for (Reward<? extends RewardSettings> reward : this.rewards) {
            reward.reward(p, this.hints);
        }
    }

    // BASIC GETTERS AND SETTERS
    public boolean triviaRunning() {
        return this.triviaRunning;
    }

    public Database getDb() {
        return db;
    }
}
