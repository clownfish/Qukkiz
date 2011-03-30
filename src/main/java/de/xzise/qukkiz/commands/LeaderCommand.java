package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;

public class LeaderCommand extends CommonHelpableSubCommand {
    
    private final Trivia plugin;
    
    public LeaderCommand(Trivia plugin) {
        super("leader", "top");
        this.plugin = plugin;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows the leaderboard." };
    }

    @Override
    public String getSmallHelpText() {
        return "Show the leader";
    }

    @Override
    public String getCommand() {
        return "/qukkiz leader";
//        return "/qukkiz leader [#page]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        int page;
        if (parameters.length == 1) {
            page = 1;
        } else if (parameters.length == 2) {
            try {
                page = Integer.parseInt(parameters[1]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ChatColor.RED + "The page has to be a non negative integer.");
                return true;
            }
            if (page < 0) {
                sender.sendMessage(ChatColor.RED + "The page has to be a non negative integer.");
                return true;
            }
        } else {
            return false;
        }
        this.plugin.sendTop(sender, page);
        return true;
    }

}
