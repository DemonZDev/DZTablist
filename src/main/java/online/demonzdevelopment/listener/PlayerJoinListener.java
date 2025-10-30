package online.demonzdevelopment.listener;

import online.demonzdevelopment.DZTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player join events
 */
public class PlayerJoinListener implements Listener {
    
    private final DZTablist plugin;
    
    public PlayerJoinListener(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize player data
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Update header/footer
            if (plugin.getHeaderFooterManager() != null) {
                plugin.getHeaderFooterManager().updateHeaderFooter(player);
            }
            
            // Update nametag
            if (plugin.getNameTagManager() != null) {
                plugin.getNameTagManager().updateNameTag(player);
            }
            
            // Update scoreboard
            if (plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().updateScoreboard(player);
            }
            
            // Update bossbar
            if (plugin.getBossbarManager() != null) {
                plugin.getBossbarManager().updateBossbars(player);
            }
            
            // Notify about updates
            if (plugin.getUpdateChecker() != null) {
                plugin.getUpdateChecker().notifyPlayerJoin(player);
            }
        }, 5L);
    }
}
