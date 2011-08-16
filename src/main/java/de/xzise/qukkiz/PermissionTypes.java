package de.xzise.qukkiz;

import org.bukkit.permissions.PermissionDefault;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.wrappers.permissions.SuperPerm;

public enum PermissionTypes implements Permission<Boolean>, SuperPerm {
    // Always possible
    ADMIN_NEXT("qukkiz.admin.next", false, "Allows you to force the next question."),
    ADMIN_HINT("qukkiz.admin.hint", false, "Allows you to force the next hint."),
    ADMIN_START("qukkiz.admin.start", false, "Allows you to start qukkiz."),
    ADMIN_STOP("qukkiz.admin.stop", false, "Allows you to stop qukkiz."),
    ADMIN_LOAD_RE("triva.admin.reload", false, "Allows you to reload the questions."),
    ADMIN_LOAD_ADD("qukkiz.load.add", false, "Allows you to load new files."),
    
    // Only while running
    NEXT("qukkiz.next", false, "Allows you to force the next question, if you enabled qukkiz for you."),
    HINT("qukkiz.hint", false, "Allows you to force the next hint, if you enabled qukkiz for you."),
    
    VOTE("qukkiz.vote", true, "Allows you to participate in the vote."),
    START_VOTE("qukkiz.startvote", true, "Allows you to start a new vote."),
    
    PLAY("qukkiz.play", true, "Allows you to play qukkiz.");

    public final String name;
    public final boolean def;
    public final String description;
    
    private PermissionTypes(String name, boolean def, String description) {
        this.name = name;
        this.def = def;
        this.description = description;
    }

    public static PermissionTypes getType(String name) {
        for (PermissionTypes type : PermissionTypes.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean getDefault() {
        return this.def;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return this.def ? PermissionDefault.TRUE : PermissionDefault.OP;
    }
}