package online.demonzdevelopment.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-world playerlist configuration handler
 */
public class PerWorldPlayerlistConfig {
    
    private final FileConfiguration config;
    
    public PerWorldPlayerlistConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public boolean isEnabled() {
        return config.getBoolean("enabled", false);
    }
    
    public List<WorldGroup> getWorldGroups() {
        List<WorldGroup> groups = new ArrayList<>();
        var section = config.getConfigurationSection("world-groups");
        if (section != null) {
            for (String groupName : section.getKeys(false)) {
                List<String> worlds = section.getStringList(groupName);
                groups.add(new WorldGroup(groupName, worlds));
            }
        }
        return groups;
    }
    
    public List<String> getUniversalWorlds() {
        return config.getStringList("universal-worlds");
    }
    
    public String getBypassPermission() {
        return config.getString("bypass-permission", "dztablist.seeall");
    }
    
    public boolean isSpectatorModeEnabled() {
        return config.getBoolean("spectator-mode.enabled", true);
    }
    
    public boolean isShowAsSpectator() {
        return config.getBoolean("spectator-mode.show-as-spectator", true);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class WorldGroup {
        private final String name;
        private final List<String> worlds;
        
        public WorldGroup(String name, List<String> worlds) {
            this.name = name;
            this.worlds = worlds;
        }
        
        public String getName() { return name; }
        public List<String> getWorlds() { return worlds; }
    }
}
