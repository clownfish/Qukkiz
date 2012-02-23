package nl.blaatz0r.Trivia;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.xzise.qukkiz.QukkizUsers;

/**
 * Handle events for all Player related events
 * 
 * @author blaatz0r
 */
public class TriviaPlayerListener implements Listener {

    private final Trivia plugin;
    private final QukkizUsers users;

    public TriviaPlayerListener(Trivia instance, QukkizUsers users) {
        super();
        this.plugin = instance;
        this.users = users;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {
        String msg = event.getMessage();
        Player player = event.getPlayer();
        if ((!event.isCancelled()) && this.plugin.isChatModeAllowed() && this.plugin.answerQuestion(msg, player, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.users.quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.users.join(event.getPlayer());
    }
}
