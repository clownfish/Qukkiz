package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class RestartCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public RestartCommand(Trivia plugin) {
        super("restart");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Restarts the server." };
    }

    @Override
    public String getSmallHelpText() {
        return "Restarts server";
    }

    @Override
    public String getCommand() {
        return "/qukkiz restart";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_START) && Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_STOP)) {
                if (this.plugin.triviaRunning()) {
                    this.plugin.stopTrivia();
                    this.plugin.startTrivia();
                } else {
                    sender.sendMessage(ChatColor.RED + "Qukkiz isn't running at the moment.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to restart the server.");
            }
            return true;
        } else {
            return false;
        }
    }

}
