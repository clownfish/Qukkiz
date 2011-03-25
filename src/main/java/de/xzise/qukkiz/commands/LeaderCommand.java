package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

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
        if (parameters.length == 1) {
            this.plugin.sendTop(sender);
            return true;
        }
        return false;
    }

}
