package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.commands.CommonHelpableSubCommand;

public class RankCommand extends CommonHelpableSubCommand {

    private Trivia plugin;
    
    public RankCommand(Trivia plugin) {
        super("rank");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows the rank of the player." };
    }

    @Override
    public String getSmallHelpText() {
        return "Show rank";
    }

    @Override
    public String getCommand() {
        return "qukkiz rank [player]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (sender instanceof Player) {
                this.plugin.sendRanking((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Only in-game players could have a rank.");
            }
            
            return true;
        } else if (parameters.length == 2) {
            this.plugin.sendRanking(MinecraftUtil.expandName(parameters[1], this.plugin.getServer()), sender);
            return true;
        }
        return false;
    }

}
