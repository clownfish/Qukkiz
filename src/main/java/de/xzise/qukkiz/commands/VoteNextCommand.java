package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionTypes;
import de.xzise.qukkiz.QukkizSettings;

public class VoteNextCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    private final QukkizSettings settings;

    public VoteNextCommand(Trivia plugin, QukkizSettings settings) {
        super("vote", "votenext");
        this.plugin = plugin;
        this.settings = settings;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Vote for the next question." };
    }

    @Override
    public String getSmallHelpText() {
        return "Vote for next question";
    }

    @Override
    public String getCommand() {
        return "qukkiz vote";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (this.plugin.triviaEnabled(sender) && plugin.isRunning()) {
                if (!Trivia.wrapper.permission(sender, PermissionTypes.VOTE)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to vote.");
                } else if (this.plugin.voted.contains(sender)) {
                    sender.sendMessage(ChatColor.BLUE + "You have already voted!");
                } else {
                    if (!this.plugin.voted.isEmpty() || Trivia.wrapper.permission(sender, PermissionTypes.START_VOTE)) {
                        this.plugin.voted.add(sender);
                        double limit = this.plugin.getUsers().getActives().size() * this.settings.voteRatio;
                        if (plugin.voted.size() > limit) {
                            plugin.nextQuestion();
                            this.plugin.getUsers().sendMessage(ChatColor.BLUE + "Vote succeeded!");
                        } else {
                            this.plugin.getUsers().sendMessage(sender, ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + " voted for the next question. [" + ChatColor.GREEN + plugin.voted.size() + "/" + (int) (Math.ceil(limit) + 1) + ChatColor.WHITE + "].");
                            if (plugin.voted.size() == 1) {
                                this.plugin.getUsers().sendMessage(sender, ChatColor.WHITE + "For the next question, use " + ChatColor.GREEN + "/qukkiz vote");
                            }
                            sender.sendMessage("You voted for the next question [" + ChatColor.GREEN + plugin.voted.size() + "/" + (int) (Math.ceil(limit) + 1) + ChatColor.WHITE + "].");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions to start a new vote.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Qukkiz is not running.");
            }

            return true;
        } else {
            return false;
        }
    }

}
