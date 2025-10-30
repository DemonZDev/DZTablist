package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Header and Footer configuration handler
 */
public class HeaderFooterConfig {
    
    private final FileConfiguration config;
    
    public HeaderFooterConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public List<String> getGlobalHeader() {
        return config.getStringList("global.header");
    }
    
    public List<String> getGlobalFooter() {
        return config.getStringList("global.footer");
    }
    
    public int getGlobalRefresh() {
        return config.getInt("global.refresh", 1000);
    }
    
    public Map<String, HeaderFooterData> getWorldConfigs() {
        Map<String, HeaderFooterData> worlds = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("worlds");
        if (section != null) {
            for (String world : section.getKeys(false)) {
                List<String> header = section.getStringList(world + ".header");
                List<String> footer = section.getStringList(world + ".footer");
                int priority = section.getInt(world + ".priority", 5);
                worlds.put(world, new HeaderFooterData(header, footer, priority));
            }
        }
        return worlds;
    }
    
    public Map<String, HeaderFooterData> getGroupConfigs() {
        Map<String, HeaderFooterData> groups = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("groups");
        if (section != null) {
            for (String group : section.getKeys(false)) {
                List<String> header = section.getStringList(group + ".header");
                List<String> footer = section.getStringList(group + ".footer");
                int priority = section.getInt(group + ".priority", 10);
                groups.put(group, new HeaderFooterData(header, footer, priority));
            }
        }
        return groups;
    }
    
    public Map<String, HeaderFooterData> getPlayerConfigs() {
        Map<String, HeaderFooterData> players = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("players");
        if (section != null) {
            for (String uuid : section.getKeys(false)) {
                List<String> header = section.getStringList(uuid + ".header");
                List<String> footer = section.getStringList(uuid + ".footer");
                int priority = section.getInt(uuid + ".priority", 20);
                players.put(uuid, new HeaderFooterData(header, footer, priority));
            }
        }
        return players;
    }
    
    public List<ConditionalHeaderFooter> getConditionalConfigs() {
        List<ConditionalHeaderFooter> conditionals = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("conditions");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String condition = section.getString(key + ".condition");
                List<String> header = section.getStringList(key + ".header");
                List<String> footer = section.getStringList(key + ".footer");
                int priority = section.getInt(key + ".priority", 15);
                conditionals.add(new ConditionalHeaderFooter(condition, header, footer, priority));
            }
        }
        return conditionals;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class HeaderFooterData {
        private final List<String> header;
        private final List<String> footer;
        private final int priority;
        
        public HeaderFooterData(List<String> header, List<String> footer, int priority) {
            this.header = header;
            this.footer = footer;
            this.priority = priority;
        }
        
        public List<String> getHeader() { return header; }
        public List<String> getFooter() { return footer; }
        public int getPriority() { return priority; }
    }
    
    public static class ConditionalHeaderFooter extends HeaderFooterData {
        private final String condition;
        
        public ConditionalHeaderFooter(String condition, List<String> header, List<String> footer, int priority) {
            super(header, footer, priority);
            this.condition = condition;
        }
        
        public String getCondition() { return condition; }
    }
}
