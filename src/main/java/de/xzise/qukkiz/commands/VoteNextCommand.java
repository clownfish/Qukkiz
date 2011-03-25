package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class VoteNextCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;

    public VoteNextCommand(Trivia plugin) {
        super("vote", "votenext");
        this.plugin = plugin;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only in-game players can vote.");
            return true;
        } else if (parameters.length == 1) {
            Player player = (Player) sender;
            if (this.plugin.triviaEnabled(player) && plugin.triviaRunning()) {
                if (!Trivia.wrapper.permission(player, PermissionTypes.VOTE)) {
                    player.sendMessage(ChatColor.RED + "You don't have permissions to vote.");
                } else if (this.plugin.voted.contains(player)) {
                    player.sendMessage(ChatColor.BLUE + "You have already voted!");
                } else {
                    if (Trivia.wrapper.permission(player, PermissionTypes.START_VOTE)) {
                        this.plugin.voted.add(player);
                        double limit = ((double) plugin.getUsers().getActives().size() / 2.0);
                        if (plugin.voted.size() > limit) {
                            plugin.nextQuestion();
                            this.plugin.getUsers().sendMessage(ChatColor.BLUE + "Vote succeeded!");
                        } else {
                            this.plugin.getUsers().sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " voted for the next question. [" + ChatColor.GREEN + plugin.voted.size() + "/" + (int) (Math.ceil(limit) + 1) + ChatColor.WHITE + "].", sender);
                            if (plugin.voted.size() == 1) {
                                this.plugin.getUsers().sendMessage(ChatColor.WHITE + "For the next question, use " + ChatColor.GREEN + "/qukkiz vote", sender);
                            }
                            player.sendMessage("You voted for the next question [" + ChatColor.GREEN + plugin.voted.size() + "/" + (int) (Math.ceil(limit) + 1) + ChatColor.WHITE + "].");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to start a new vote.");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Trivia is not running.");
            }

            return true;
        } else {
            return false;
        }
    }

}
