package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nametag configuration handler
 */
public class NameTagConfig {
    
    private final FileConfiguration config;
    
    public NameTagConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public String getGlobalPrefix() {
        return config.getString("global.prefix", "&7");
    }
    
    public String getGlobalSuffix() {
        return config.getString("global.suffix", "");
    }
    
    public String getGlobalVisibility() {
        return config.getString("global.visibility", "ALWAYS");
    }
    
    public String getGlobalCollisionRule() {
        return config.getString("global.collision-rule", "ALWAYS");
    }
    
    public boolean isGlobalInvisible() {
        return config.getBoolean("global.invisible", false);
    }
    
    public Map<String, NameTagData> getGroupConfigs() {
        Map<String, NameTagData> groups = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("groups");
        if (section != null) {
            for (String group : section.getKeys(false)) {
                String prefix = section.getString(group + ".prefix", "");
                String suffix = section.getString(group + ".suffix", "");
                int priority = section.getInt(group + ".priority", 1);
                groups.put(group, new NameTagData(prefix, suffix, priority));
            }
        }
        return groups;
    }
    
    public boolean isAntiOverrideEnabled() {
        return config.getBoolean("anti-override.enabled", true);
    }
    
    public boolean isFixInvisibleNametags() {
        return config.getBoolean("client-bug-fixes.fix-1-8-invisible-nametags", true);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class NameTagData {
        private final String prefix;
        private final String suffix;
        private final int priority;
        
        public NameTagData(String prefix, String suffix, int priority) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.priority = priority;
        }
        
        public String getPrefix() { return prefix; }
        public String getSuffix() { return suffix; }
        public int getPriority() { return priority; }
    }
}
