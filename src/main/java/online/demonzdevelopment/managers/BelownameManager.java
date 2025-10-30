package online.demonzdevelopment.managers;

import online.demonzdevelopment.DZTablist;

/**
 * Manages belowname display
 */
public class BelownameManager {
    
    private final DZTablist plugin;
    
    public BelownameManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        plugin.getLogger().info("Belowname manager initialized");
    }
    
    public void reload() {}
    public void shutdown() {}
}
