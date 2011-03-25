package nl.blaatz0r.Trivia;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import de.xzise.qukkiz.QukkizUsers;

/**
 * Handle events for all Player related events
 * @author blaatz0r
 */
public class TriviaPlayerListener extends PlayerListener {
    
    private final Trivia plugin;
    private final QukkizUsers users;
    
    public TriviaPlayerListener(Trivia instance, QukkizUsers users) {
        super();
        plugin = instance;
        this.users = users;
    }
    
    public void onPlayerChat(PlayerChatEvent event) {
    	String msg = event.getMessage();
    	Player player = event.getPlayer();
    	if((!event.isCancelled()) && plugin.triviaEnabled(player) && plugin.answerQuestion(msg, player)) {
    	    event.setCancelled(true);
    	}
    }
    
    public void onPlayerQuit(PlayerEvent event) {
        this.users.quit(event.getPlayer());
    }
    
    public void onPlayerJoin(PlayerEvent event) {
        this.users.join(event.getPlayer());
    }
}

