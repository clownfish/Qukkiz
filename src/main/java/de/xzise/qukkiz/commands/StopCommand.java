package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionTypes;

public class StopCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public StopCommand(Trivia plugin) {
        super("stop");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Stops the server." };
    }

    @Override
    public String getSmallHelpText() {
        return "Stops server";
    }

    @Override
    public String getCommand() {
        return "qukkiz stop";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_STOP)) {
                if (this.plugin.isRunning()) {
                    this.plugin.stopTrivia();
                } else {
                    sender.sendMessage(ChatColor.RED + "Qukkiz isn't running at the moment.");
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
