package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionTypes;

public class StoreCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public StoreCommand(Trivia plugin) {
        super("subscribe", "store");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Subscribes qukkiz for the player, so qukkiz will automatically start" , "To remove simply execute again." };
    }

    @Override
    public String getSmallHelpText() {
        return "Subscribe player";
    }

    @Override
    public String getCommand() {
        return "qukkiz subscribe";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.PLAY)) {
                if (sender instanceof Player) {
                    if (this.plugin.getUsers().toogleStorage((Player) sender)) {
                        sender.sendMessage(ChatColor.GREEN + ((Player) sender).getName() + ChatColor.WHITE + " subscribed qukkiz.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + ((Player) sender).getName() + ChatColor.WHITE + " unsubscribed qukkiz");
                    }
                    this.plugin.getUsers().storeUsers();
                } else {
                    sender.sendMessage(ChatColor.RED + "Only in-game players could subscribe qukkiz.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You have not the permission to subscribe Qukkiz.");
            }
            return true;
        } else {
            return false;
        }
    }

}
