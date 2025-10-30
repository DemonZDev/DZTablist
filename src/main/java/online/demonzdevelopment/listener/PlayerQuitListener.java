package online.demonzdevelopment.listeners;

import online.demonzdevelopment.DZTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player quit events
 */
public class PlayerQuitListener implements Listener {
    
    private final DZTablist plugin;
    
    public PlayerQuitListener(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Cleanup player data
        if (plugin.getBossbarManager() != null) {
            // Bossbars are automatically hidden when player quits
        }
        
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().hideScoreboard(player);
        }
    }
}
