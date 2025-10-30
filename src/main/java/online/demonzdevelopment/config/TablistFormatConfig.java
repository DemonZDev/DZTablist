package online.demonzdevelopment.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Tablist format configuration handler
 */
public class TablistFormatConfig {
    
    private final FileConfiguration config;
    
    public TablistFormatConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public String getGlobalPrefix() {
        return config.getString("global.prefix", "&7");
    }
    
    public String getGlobalName() {
        return config.getString("global.name", "%player_name%");
    }
    
    public String getGlobalSuffix() {
        return config.getString("global.suffix", "");
    }
    
    public int getGlobalRefresh() {
        return config.getInt("global.refresh", 500);
    }
    
    public boolean isAntiOverrideEnabled() {
        return config.getBoolean("anti-override.enabled", true);
    }
    
    public String getAntiOverridePriority() {
        return config.getString("anti-override.priority", "HIGHEST");
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
