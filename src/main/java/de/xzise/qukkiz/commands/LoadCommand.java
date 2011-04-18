package de.xzise.qukkiz.commands;

import java.io.File;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class LoadCommand extends CommonHelpableSubCommand {

    private final Trivia plugin;
    
    public LoadCommand(Trivia plugin) {
        super("load");
        this.plugin = plugin;
    }
    
    @Override
    public String[] getFullHelpText() {
        return new String[] { "Reload the questions and load new questions." };
    }

    @Override
    public String getSmallHelpText() {
        return "(Re)loads questions";
    }

    @Override
    public String getCommand() {
        return "qukkiz load [file]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_LOAD_RE)) {
                this.plugin.loadQuestions(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to reload the questions.");
            }
        } else {
            if (Trivia.wrapper.permission(sender, PermissionTypes.ADMIN_LOAD_ADD)) {
                //TODO: Concat other parameters
                this.plugin.loadQuestions(new File(this.plugin.getDataFolder(), parameters[2]), sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to add a questionsfile.");
            }
        } 
        return true;
    }

}
