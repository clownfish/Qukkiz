package de.xzise.qukkiz.commands;

import java.util.ArrayList;
import java.util.List;

import nl.blaatz0r.Trivia.Trivia;

import de.xzise.commands.CommonCommandMap;
import de.xzise.commands.CommonHelpCommand;
import de.xzise.commands.HelpCommand;
import de.xzise.commands.SubCommand;
import de.xzise.qukkiz.QukkizSettings;

public class CommandMap extends CommonCommandMap {
    
    public CommandMap(Trivia plugin, QukkizSettings settings) {
        super();
        
        HelpCommand helper = new CommonHelpCommand("Qukkiz");
        SubCommand enable = new EnableCommand(plugin);
        
        List<SubCommand> commands = new ArrayList<SubCommand>();
        commands.add(new AnswerCommand(plugin));
        commands.add(enable);
        commands.add(helper);
        commands.add(new RankCommand(plugin));
        commands.add(new LeaderCommand(plugin));
        commands.add(new VoteNextCommand(plugin, settings));
        commands.add(new StoreCommand(plugin));
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
