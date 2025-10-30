package online.demonzdevelopment.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Playerlist objective configuration handler
 */
public class PlayerlistObjectiveConfig {
    
    private final FileConfiguration config;
    
    public PlayerlistObjectiveConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }
    
    public String getType() {
        return config.getString("type", "HEARTS");
    }
    
    public String getPlaceholder() {
        return config.getString("placeholder", "%player_health%");
    }
    
    public String getText() {
        return config.getString("text", "HP");
    }
    
    public String getTitle() {
        return config.getString("title", "&c❤");
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
