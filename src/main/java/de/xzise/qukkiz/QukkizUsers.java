package de.xzise.qukkiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;

public class QukkizUsers {

    private List<String> stored = new ArrayList<String>();
    private List<CommandSender> active = new ArrayList<CommandSender>();

    private boolean createFile(File f) {
        if (!f.exists()) {
            try {
                Trivia.logger.info("The users file doesn't exists! Create new.");
                return f.createNewFile();
            } catch (IOException e) {
                Trivia.logger.warning("Unable to create the users file.", e);
                return false;
            }
        } else {
            return f.isFile();
        }
    }

    public void readFile(File f) {
        if (this.createFile(f)) {
            Scanner scanner;
            try {
                scanner = new Scanner(f);
                try {

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (MinecraftUtil.isSet(line)) {
                            this.stored.add(line);
                        }
                    }

                } finally {
                    scanner.close();
                }
            } catch (FileNotFoundException e) {
                Trivia.logger.warning("Unable to read the users file.", e);
            }
        } else {
            Trivia.logger.warning("Unable to read the users file.");
        }
    }

    public void storeUsers(File f) {
        if (this.createFile(f)) {
            try {
                FileWriter fw = new FileWriter(f);
                try {
                    for (String entry : this.stored) {
                        fw.write(entry + "\n");
                    }
                } finally {
                    fw.close();
                }
            } catch (IOException e) {
                Trivia.logger.warning("Unable to store the users.", e);
            }
        } else {
            Trivia.logger.warning("Unable to read the users file.");
        }
    }

    public boolean toogleSender(CommandSender sender) {
        return MinecraftUtil.toogleEntry(sender, this.active);
    }

    public boolean toogleStorage(Player player) {
        return MinecraftUtil.toogleEntry(player.getName(), this.stored);
    }

    public void sendMessage(String message) {
        this.sendMessage(message, null);
    }

    public void sendMessage(String message, CommandSender except) {
        for (CommandSender user : this.active) {
            if (!user.equals(except)) {
                user.sendMessage(message);
            }
        }
    }

    public List<CommandSender> getActives() {
        return this.active;
    }

    public void quit(Player player) {
        this.active.remove(player);
    }

    public void join(Player player) {
        if (this.stored.contains(player.getName())) {
            this.active.add(player);
        }
    }

}
