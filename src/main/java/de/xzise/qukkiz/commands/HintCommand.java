package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class HintCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;

    public HintCommand(Trivia plugin) {
        super("hint");
        this.plugin = plugin;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows the next hint." };
    }

    @Override
    public String getSmallHelpText() {
        return "Show next hint";
    }

    @Override
    public String getCommand() {
        return "qukkiz hint";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (this.plugin.isRunning()) {
                if (this.plugin.permission(sender, PermissionTypes.HINT, PermissionTypes.ADMIN_HINT)) {
                    plugin.updateHint();
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no permission to update the hint.");
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
