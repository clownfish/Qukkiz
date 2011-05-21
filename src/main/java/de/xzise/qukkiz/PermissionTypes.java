package de.xzise.qukkiz;

import de.xzise.wrappers.permissions.Permission;

public enum PermissionTypes implements Permission<Boolean> {
    // Always possible
    ADMIN_NEXT("trivia.admin.next", false),
    ADMIN_HINT("trivia.admin.hint", false),
    ADMIN_START("trivia.admin.start", false),
    ADMIN_STOP("trivia.admin.stop", false),
    ADMIN_LOAD_RE("triva.admin.reload", false),
    ADMIN_LOAD_ADD("trivia.load.add", false),
    
    // Only while running
    NEXT("trivia.next", false),
    HINT("trivia.hint", false),
    
    VOTE("trivia.vote", true),
    START_VOTE("trivia.startvote", true),
    
    PLAY("trivia.play", true);

    public final String name;
    public final boolean def;
    
    private PermissionTypes(String name, boolean def) {
        this.name = name;
        this.def = def;
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
}