package de.xzise.qukkiz;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.MinecraftUtil;

public class PermissionWrapper {

    public enum PermissionTypes {
        ADMIN_NEXT("trivia.admin.next"),
        ADMIN_HINT("trivia.admin.hint"),
        ADMIN_START("trivia.admin.start"),
        ADMIN_STOP("trivia.admin.stop"),
        ADMIN_LOAD_RE("triva.admin.reload"),
        ADMIN_LOAD_ADD("trivia.load.add"),
        
        NEXT("trivia.next"),
        HINT("trivia.hint"),
        
        VOTE("trivia.vote"),
        START_VOTE("trivia.startvote"),
        
        PLAY("trivia.play");

        public final String name;

        PermissionTypes(String name) {
            this.name = name;
        }

        public static PermissionTypes getType(String name) {
            for (PermissionTypes type : PermissionTypes.values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static PermissionTypes[] ADMIN_PERMISSIONS = new PermissionTypes[] { PermissionTypes.ADMIN_NEXT, PermissionTypes.ADMIN_HINT, };

    private static PermissionTypes[] DEFAULT_PERMISSIONS = new PermissionTypes[] { PermissionTypes.VOTE, PermissionTypes.START_VOTE, PermissionTypes.PLAY };

    private PermissionHandler handler = null;


    private boolean permissionInternal(CommandSender sender, PermissionTypes permission) {
        if (MinecraftUtil.contains(permission, DEFAULT_PERMISSIONS)) {
            return true;
        } else if (MinecraftUtil.contains(permission, ADMIN_PERMISSIONS)) {
            return sender.isOp();
        } else {
            return false;
        }
    }

    public boolean permission(CommandSender sender, PermissionTypes permission) {
        if (sender instanceof Player) {
            if (this.handler != null) {
                return this.handler.has((Player) sender, permission.name);
            } else {
                return this.permissionInternal(sender, permission);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            return true;
        } else {
            return this.permissionInternal(sender, permission);
        }
    }

    public boolean hasAdminPermission(CommandSender sender) {
        return this.permissionOr(sender, ADMIN_PERMISSIONS);
    }

    public boolean permissionOr(CommandSender sender, PermissionTypes... permission) {
        for (PermissionTypes permissionType : permission) {
            if (this.permission(sender, permissionType)) {
                return true;
            }
        }
        return false;
    }

    public boolean permissionAnd(CommandSender sender, PermissionTypes... permission) {
        for (PermissionTypes permissionType : permission) {
            if (!this.permission(sender, permissionType)) {
                return false;
            }
        }
        return true;
    }

    public void init(Plugin plugin) {
        this.handler = null;
        if (plugin != null) {
            if (plugin.isEnabled()) {
                this.handler = ((Permissions) plugin).getHandler();
                Trivia.logger.info("Permissions enabled.");
            } else {
                Trivia.logger.info("Permissions system found, but not enabled. Use defaults.");
            }
        } else {
            Trivia.logger.warning("Permission system not found. Use defaults.");
        }
    }

    public boolean useOfficial() {
        return this.handler != null;
    }

}
