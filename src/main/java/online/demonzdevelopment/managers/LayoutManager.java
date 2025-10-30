package online.demonzdevelopment.managers;

import online.demonzdevelopment.DZTablist;

/**
 * Manages custom tablist layouts
 */
public class LayoutManager {
    
    private final DZTablist plugin;
    
    public LayoutManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        plugin.getLogger().info("Layout manager initialized");
    }
    
    public void reload() {}
    public void shutdown() {}
}
