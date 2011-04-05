package nl.blaatz0r.Trivia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
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
import de.xzise.qukkiz.hinter.Answer;
import de.xzise.qukkiz.questioner.Questioner;
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
    private Questioner questioner;
    private CommandMap commands;
    private QukkizUsers users;
    private List<Reward<? extends RewardSettings>> rewards;
    private QukkizSettings settings;
    private Timer timer;
    private TimerTask task;

    private Map<Player, Answer> answers = new HashMap<Player, Answer>();
    
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
            public void onPluginEnable(PluginEnableEvent event) {
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
            public void onPluginDisable(PluginDisableEvent event) {
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
        this.answers.clear();
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
            this.proposeWinner();
        } else {
            if (this.task != null) {
                this.task.cancel();
            }
            this.hints++;
            this.questioner.getHinter().nextHint();

            this.sendQuestion();

            this.startHintTimer();
        }
    }
    
    private void noAnswer() {
        this.users.sendMessage(ChatColor.RED + "Nobody" + ChatColor.WHITE + " got it right. The answer was " + ChatColor.GREEN + this.questioner.getQuestion().getAnswer());
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
        this.questioner = MinecraftUtil.getRandom(this.questions).createHinter();
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
            while (rs.next()) {
                ChatColor color = ChatColor.GREEN;
                switch (i) {
                case 1 :
                    color = ChatColor.GOLD;
                    break;
                case 2:
                    color = ChatColor.GRAY;
                    break;
//                case 3:
//                    color =
                }
                String q = color + MinecraftUtil.getOrdinal(i) + ChatColor.WHITE + ") " + ChatColor.GREEN + rs.getString("name") + ChatColor.WHITE + " with " + ChatColor.GREEN + rs.getInt("score") + ChatColor.WHITE + " points";
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

    public boolean answerQuestion(String answer, Player player) {
        if (this.canAnswer) {
            if (this.questioner.putAnswer(new Answer(new Date().getTime() - this.startTime, this.hints, answer, player))) {
                this.proposeWinner();
            } else {
                return false;
            }
            return true;            
        } else {
            return false;
        }
    }
    
    private void proposeWinner(Answer answer) {
        this.canAnswer = false;

        double time = Math.round(answer.time / 10) / 100;

        this.users.sendMessage(ChatColor.DARK_GREEN + MinecraftUtil.getRandom(grats) + ChatColor.GREEN + answer.player.getDisplayName() + ChatColor.DARK_GREEN + " got the answer in " + ChatColor.GREEN + String.valueOf(time) + ChatColor.DARK_GREEN + " seconds!");
        this.users.sendMessage(ChatColor.DARK_GREEN + "The answer was " + ChatColor.GREEN + this.questioner.getQuestion().getAnswer());

        this.reward(answer);
        this.nextQuestion();
    }
    
    private void proposeWinner() {
        Answer a = this.questioner.getBestAnswer();
        if (a == null) {
            this.noAnswer();
        } else {
            this.proposeWinner(a);
        }
    }

    public void reward(Answer answer) {
        for (Reward<? extends RewardSettings> reward : this.rewards) {
            reward.reward(answer);
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
