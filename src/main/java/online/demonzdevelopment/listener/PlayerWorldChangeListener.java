package online.demonzdevelopment.listeners;

import online.demonzdevelopment.DZTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Handles player world change events
 */
public class PlayerWorldChangeListener implements Listener {
    
    private final DZTablist plugin;
    
    public PlayerWorldChangeListener(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        
        // Update all features
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getHeaderFooterManager() != null) {
                plugin.getHeaderFooterManager().updateHeaderFooter(player);
            }
            
            if (plugin.getNameTagManager() != null) {
                plugin.getNameTagManager().updateNameTag(player);
            }
            
            if (plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().updateScoreboard(player);
            }
            
            if (plugin.getBossbarManager() != null) {
                plugin.getBossbarManager().updateBossbars(player);
            }
        }, 2L);
    }
}
