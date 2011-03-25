package de.xzise.qukkiz.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.commands.CommandMap;
import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.commands.FullHelpable;
import de.xzise.commands.HelpCommand;
import de.xzise.commands.SmallHelpable;
import de.xzise.commands.SubCommand;

public class HelperCommand extends CommonHelpableSubCommand implements HelpCommand {

    private CommandMap map;

    public HelperCommand() {
        super("help", "?");
    }

    private String[] getFullHelp(FullHelpable helpable) {
        List<String> lines = new ArrayList<String>();
        lines.add("Qukkiz help: " + ChatColor.GREEN + helpable.getCommand());
        for (String string : helpable.getFullHelpText()) {
            lines.add(string);
        }
        if (this.commands.length > 1) {
            String aliases = "Aliases: ";
            for (int i = 1; i < helpable.getCommands().length; i++) {
                aliases += ChatColor.GREEN + this.commands[i];
                if (i < this.commands.length - 1) {
                    aliases += ChatColor.WHITE + ", ";
                }
            }
            lines.add(aliases);
        }
        return lines.toArray(new String[0]);
    }

    private final String getSmallHelp(SmallHelpable helpable) {
        return ChatColor.GREEN + helpable.getCommand() + ChatColor.WHITE + " - " + helpable.getSmallHelpText();
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length > 2) {
            return false;
        }

        List<SubCommand> commands = this.map.getCommandList();

        // First get all commands:
        List<String> lines = new ArrayList<String>(commands.size());
        for (SubCommand command : commands) {
            if (command instanceof SmallHelpable && ((SmallHelpable) command).listHelp(sender)) {
                lines.add(this.getSmallHelp((SmallHelpable) command));
            }
        }

        Integer page = null;
        int maxPage = lines.size() / (MinecraftUtil.MAX_LINES_VISIBLE - 1);
        if (parameters.length == 2) {
            if ((page = MinecraftUtil.tryAndGetInteger(parameters[1])) != null) {
                if (page < 1) {
                    sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                    return true;
                } else if (page > maxPage) {
                    sender.sendMessage(ChatColor.RED + "There are only 2 pages of help");
                    return true;
                }
            } else {
                SubCommand command = this.map.getCommand(parameters[1]);
                if (command instanceof FullHelpable) {
                    this.showCommandHelp(sender, (FullHelpable) command);
                } else {
                    sender.sendMessage(ChatColor.RED + "Please input a valid number/command");
                }
                return true;
            }
        }
        if (page == null) {
            page = 1;
        }
        sender.sendMessage(ChatColor.WHITE + "------------------ " + ChatColor.GREEN + "Qukkiz Help " + page + "/" + maxPage + ChatColor.WHITE + " ------------------");
        for (int i = (page - 1) * (MinecraftUtil.MAX_LINES_VISIBLE - 1); i < lines.size() && i < page * (MinecraftUtil.MAX_LINES_VISIBLE - 1); i++) {
            sender.sendMessage(lines.get(i));
        }
        return true;
    }

    public void showCommandHelp(CommandSender sender, FullHelpable command) {
        sender.sendMessage("Qukkiz Help");
        for (String line : this.getFullHelp(command)) {
            sender.sendMessage(line);
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows the selected help page." };
    }

    @Override
    public String getSmallHelpText() {
        return "Shows the help";
    }

    @Override
    public String getCommand() {
        return "qukkiz help [#page]";
    }

    @Override
    public void setCommandMap(CommandMap map) {
        this.map = map;
    }

}
