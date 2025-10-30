package online.demonzdevelopment.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Belowname configuration handler
 */
public class BelownameConfig {
    
    private final FileConfiguration config;
    
    public BelownameConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }
    
    public String getTitle() {
        return config.getString("title", "Level");
    }
    
    public String getPlaceholder() {
        return config.getString("placeholder", "%player_level%");
    }
    
    public int getRefresh() {
        return config.getInt("refresh", 1000);
    }
    
    public List<String> getConditions() {
        return config.getStringList("conditions");
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
