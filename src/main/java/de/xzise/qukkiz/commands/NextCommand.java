package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionTypes;

public class NextCommand extends CommonHelpableSubCommand {
    
    private Trivia plugin;
    
    public NextCommand(Trivia plugin) {
        super("next");
        this.plugin = plugin;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Discards the active question and search for a new one." };
    }

    @Override
    public String getSmallHelpText() {
        return "Select next question";
    }

    @Override
    public String getCommand() {
        return "qukkiz next";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (plugin.isRunning()) {
                if (this.plugin.permission(sender, PermissionTypes.NEXT, PermissionTypes.ADMIN_NEXT)) {
                    this.plugin.nextQuestion();
                    
                    this.plugin.getUsers().sendMessage(ChatColor.BLUE + "Next question coming up!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no permission to select the next question.");
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
