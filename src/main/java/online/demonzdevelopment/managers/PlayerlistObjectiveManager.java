package online.demonzdevelopment.managers;

import online.demonzdevelopment.DZTablist;

/**
 * Manages playerlist objective (right-side display)
 */
public class PlayerlistObjectiveManager {
    
    private final DZTablist plugin;
    
    public PlayerlistObjectiveManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        plugin.getLogger().info("Playerlist objective manager initialized");
    }
    
    public void reload() {}
    public void shutdown() {}
}
