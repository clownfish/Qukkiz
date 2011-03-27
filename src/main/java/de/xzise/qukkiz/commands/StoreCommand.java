package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;

public class StoreCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public StoreCommand(Trivia plugin) {
        super("store");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Stores the player and qukkiz will automatically start." , "To remove simply execute again." };
    }

    @Override
    public String getSmallHelpText() {
        return "Store player";
    }

    @Override
    public String getCommand() {
        return "/qukkiz store";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (sender instanceof Player) {
                if (this.plugin.getUsers().toogleStorage((Player) sender)) {
                    sender.sendMessage("Qukkiz stored " + ChatColor.GREEN + ((Player) sender).getName() + ChatColor.WHITE + ".");
                } else {
                    sender.sendMessage("Qukkiz removed " + ChatColor.GREEN + ((Player) sender).getName() + ChatColor.WHITE + ".");
                }
                this.plugin.getUsers().storeUsers();
            } else {
                sender.sendMessage(ChatColor.RED + "Only in-game players could store if qukkiz is used.");
            }
            return true;
        } else {
            return false;
        }
    }

}