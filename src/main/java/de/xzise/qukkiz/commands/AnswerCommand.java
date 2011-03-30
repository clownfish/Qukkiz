package de.xzise.qukkiz.commands;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;

public class AnswerCommand extends CommonHelpableSubCommand {
    
    private Trivia plugin;
    
    public AnswerCommand(Trivia plugin) {
        super("answer", "a");
        this.plugin = plugin;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Propose an answer for the given question" };
    }

    @Override
    public String getSmallHelpText() {
        return "Propose answer";
    }

    @Override
    public String getCommand() {
        return "qukkiz answer <answer>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (sender instanceof Player) {
            if (parameters.length >= 2) {
                String answer = parameters[1];
                for (int i = 2; i < parameters.length; i++) {
                    answer += " " + parameters[i];
                }
                // TODO: Answer here
                this.plugin.answerQuestion(answer, (Player) sender);
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only in-game players can answer questions.");
            return true;
        }
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] permissionsNeeded() {
        return new String[] { "trivia.play" };
    }
    
}
