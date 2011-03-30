package de.xzise.qukkiz.hinter;

import org.bukkit.entity.Player;

public class Answer {
    
    public final int hint;
    public final long time;
    public final String answer;
    public final Player player;
    
    public Answer(long time, int hint, String answer, Player player) {
        this.time = time;
        this.hint = hint;
        this.answer = answer;
        this.player = player;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Answer) {
            return this.player.equals(((Answer) o).player);
        } else {
            return false;
        }
    }

}
