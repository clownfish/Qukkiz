package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class StartCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public StartCommand(Trivia plugin) {
        super("start");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Starts the server." };
    }

    @Override
    public String getSmallHelpText() {
        return "Starts server";
    }

    @Override
    public String getCommand() {
        return "/qukkiz start";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_START)) {
                if (!this.plugin.triviaRunning()) {
                    this.plugin.startTrivia();
                } else {
                    sender.sendMessage(ChatColor.RED + "Qukkiz is already running.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to stop the server.");
            }
            return true;
        } else {
            return false;
        }
    }

}
