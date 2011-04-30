package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

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
    public String[] permissionsNeeded() {
        return new String[] { PermissionTypes.PLAY.name };
    }
    
    @Override
    public boolean listHelp(CommandSender sender) {
        return Trivia.wrapper.permission(sender, PermissionTypes.PLAY);
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.PLAY)) {
                if (this.plugin.isRunning()) {
                    if (this.plugin.getUsers().toogleSender(sender)) {
                        sender.sendMessage("Qukkiz is now " + ChatColor.GREEN + "enabled" + ChatColor.WHITE + ".");
                        this.plugin.sendQuestion(sender);
                    } else {
                        sender.sendMessage("Qukkiz is now " + ChatColor.GREEN + "disabled" + ChatColor.WHITE + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Qukkiz is not running at the moment");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You have not the permission to play Qukkiz.");
            }
            return true;
        } else {
            return false;
        }
    }

}
