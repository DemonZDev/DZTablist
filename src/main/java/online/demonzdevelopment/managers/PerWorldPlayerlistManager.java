package online.demonzdevelopment.managers;

import online.demonzdevelopment.DZTablist;

/**
 * Manages per-world playerlist
 */
public class PerWorldPlayerlistManager {
    
    private final DZTablist plugin;
    
    public PerWorldPlayerlistManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        plugin.getLogger().info("Per-world playerlist manager initialized");
    }
    
    public void reload() {}
    public void shutdown() {}
}
