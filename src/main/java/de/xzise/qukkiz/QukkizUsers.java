package de.xzise.qukkiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;

public class QukkizUsers {

    private List<String> stored = new ArrayList<String>();
    private List<CommandSender> active = new ArrayList<CommandSender>();
    
    private final File file;
    
    public QukkizUsers(File file) {
        this.file = file;
    }

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

    public void readFile() {
        this.readFile(this.file);
    }
    
    public void readFile(Server server) {
        this.readFile(this.file, server);
    }
    
    public void readFile(File f) {
        this.readFile(f, null);
    }
    
    public void readFile(File f, Server server) {
        if (this.createFile(f)) {
            Scanner scanner;
            try {
                scanner = new Scanner(f);
                try {
                    this.stored.clear();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (MinecraftUtil.isSet(line)) {
                            this.stored.add(line);
                        }
                    }
                    if (server != null) {
                        for (String user : this.stored) {
                            Player p = server.getPlayer(user);
                            if (p != null) {
                                this.active.add(p);
                            }
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

    public void storeUsers() {
        this.storeUsers(this.file);
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

    public void sendMessage(String... message) {
        this.sendMessage(null, message);
    }

    public void sendMessage(CommandSender except, String... message) {
        if (message.length == 0) {
            return;
        }
        for (CommandSender user : this.active) {
            if (!user.equals(except)) {
                for (String messageLine : message) {
                    user.sendMessage(messageLine);
                }
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
