package online.demonzdevelopment.listeners;

import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import online.demonzdevelopment.DZTablist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Handles LuckPerms events
 */
public class LuckPermsListener {
    
    private final DZTablist plugin;
    
    public LuckPermsListener(DZTablist plugin) {
        this.plugin = plugin;
        registerEvents();
    }
    
    /**
     * Register LuckPerms events
     */
    private void registerEvents() {
        plugin.getLuckPerms().getEventBus().subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculate);
        plugin.getLuckPerms().getEventBus().subscribe(GroupDataRecalculateEvent.class, this::onGroupDataRecalculate);
    }
    
    /**
     * Handle user data recalculation (when permissions/groups change)
     */
    private void onUserDataRecalculate(UserDataRecalculateEvent event) {
        Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
        if (player == null) return;
        
        // Update all features
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getHeaderFooterManager() != null) {
                plugin.getHeaderFooterManager().updateHeaderFooter(player);
            }
            
            if (plugin.getNameTagManager() != null) {
                plugin.getNameTagManager().updateNameTag(player);
            }
            
            if (plugin.getSortingManager() != null) {
                plugin.getSortingManager().updateSorting(player);
            }
        }, 2L);
    }
    
    /**
     * Handle group data recalculation
     */
    private void onGroupDataRecalculate(GroupDataRecalculateEvent event) {
        // Update all online players in this group
        for (Player player : Bukkit.getOnlinePlayers()) {
            String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                    .getPrimaryGroup();
            if (group.equals(event.getGroup().getName())) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (plugin.getHeaderFooterManager() != null) {
                        plugin.getHeaderFooterManager().updateHeaderFooter(player);
                    }
                    
                    if (plugin.getNameTagManager() != null) {
                        plugin.getNameTagManager().updateNameTag(player);
                    }
                }, 2L);
            }
        }
    }
}
