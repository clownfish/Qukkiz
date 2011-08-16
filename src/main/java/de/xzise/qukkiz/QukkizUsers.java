package de.xzise.qukkiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;

public class QukkizUsers {

    private List<String> stored = new ArrayList<String>();
    private List<CommandSender> active = new ArrayList<CommandSender>();
    private boolean optInEnable;
    private boolean running;
    
    private final File file;
    private final Server server;
    
    public QukkizUsers(File file, Server server) {
        this.file = file;
        this.server = server;
        this.optInEnable = true;
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
    
    public void readFile(File f) {
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
                    for (Player player : this.server.getOnlinePlayers()) {
                        this.join(player);
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
        // If optOut store acts negative (like unsubscribe)
        if (this.stored.contains(player.getName()) ^ !this.optInEnable) {
            if (Trivia.wrapper.permission(player, PermissionTypes.PLAY) && this.running) {
                if (!this.active.contains(player)) {
                    this.active.add(player);
                    player.sendMessage("Qukkiz is now " + ChatColor.GREEN + "enabled" + ChatColor.WHITE + ".");
                }
            }
        }
    }

    public int getAnsweringSize() {
        int count = 0;
        for (CommandSender sender : this.active) {
            if (sender instanceof Player) {
                count++;
            }
        }
        return count;
    }

    public void setOptInEnable(boolean optInEnable) {
        this.optInEnable = optInEnable;
    }

    public boolean isOptInEnable() {
        return this.optInEnable;
    }

    public void run() {
        this.running = true;
        this.readFile();
    }

    public void stop() {
        this.running = false;
    }

    public boolean isPlaying(CommandSender sender) {
        return this.getActives().contains(sender);
    }
}
