package online.demonzdevelopment.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Layout configuration handler
 */
public class LayoutConfig {
    
    private final FileConfiguration config;
    
    public LayoutConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public boolean isEnabled() {
        return config.getBoolean("enabled", false);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
