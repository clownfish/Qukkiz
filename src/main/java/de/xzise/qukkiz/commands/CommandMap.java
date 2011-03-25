package de.xzise.qukkiz.commands;

import java.util.ArrayList;
import java.util.List;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonCommandMap;
import de.xzise.commands.HelpCommand;
import de.xzise.commands.SubCommand;
import de.xzise.qukkiz.PermissionWrapper.PermissionTypes;

public class CommandMap extends CommonCommandMap {

    @Override
    public boolean executeCommand(CommandSender sender, String[] parameters) {
        if (Trivia.wrapper.permission(sender, PermissionTypes.PLAY)) {
            return super.executeCommand(sender, parameters);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute any command.");
            return true;
        }
    }
    
    public CommandMap(Trivia plugin) {
        super();
        
        HelpCommand helper = new HelperCommand();
        SubCommand enable = new EnableCommand(plugin);
        
        List<SubCommand> commands = new ArrayList<SubCommand>();
        commands.add(new AnswerCommand(plugin));
        commands.add(enable);
        commands.add(helper);
        commands.add(new RankCommand(plugin));
        commands.add(new LeaderCommand(plugin));
        commands.add(new VoteNextCommand(plugin));
        commands.add(new StoreCommand(plugin.getUsers()));
        commands.add(new HintCommand(plugin));
        commands.add(new LoadCommand(plugin));
        commands.add(new NextCommand(plugin));
        commands.add(new RestartCommand(plugin));
        commands.add(new StartCommand(plugin));
        commands.add(new StopCommand(plugin));
        
        this.populate(commands);
        this.setHelper(helper);
        this.setDefault(enable);
    }
    
}
