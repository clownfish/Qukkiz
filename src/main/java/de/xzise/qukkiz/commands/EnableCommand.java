package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;

public class EnableCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;

    public EnableCommand(Trivia plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Enables qukkiz for the player." };
    }

    @Override
    public String getSmallHelpText() {
        return "En/Disable qukkiz";
    }

    @Override
    public String getCommand() {
        return "qukkiz";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            if (this.plugin.triviaRunning()) {
                if (this.plugin.getUsers().toogleSender(sender)) {
                    sender.sendMessage("Qukkiz is now " + ChatColor.GREEN + "enabled" + ChatColor.WHITE + ".");
                    this.plugin.sendQuestion(sender);
                } else {
                    sender.sendMessage("Qukkiz is now " + ChatColor.GREEN + "disabled" + ChatColor.WHITE + ".");
                }
            } else {
                sender.sendMessage("Trivia is not running at the moment");
            }

            return true;
        } else {
            return false;
        }
    }

}
