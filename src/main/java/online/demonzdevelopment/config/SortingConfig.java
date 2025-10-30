package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sorting configuration handler
 */
public class SortingConfig {
    
    private final FileConfiguration config;
    
    public SortingConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public List<SortingRule> getSortingRules() {
        List<SortingRule> rules = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("rules");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String type = section.getString(key + ".type", "GROUP");
                int priority = section.getInt(key + ".priority", 1);
                
                SortingRule rule = new SortingRule(type, priority);
                
                if (type.equals("GROUP")) {
                    ConfigurationSection groups = section.getConfigurationSection(key + ".groups");
                    if (groups != null) {
                        Map<String, Integer> groupWeights = new HashMap<>();
                        for (String group : groups.getKeys(false)) {
                            groupWeights.put(group, groups.getInt(group, 1));
                        }
                        rule.setGroupWeights(groupWeights);
                    }
                } else if (type.contains("PLACEHOLDER")) {
                    rule.setPlaceholder(section.getString(key + ".placeholder", ""));
                    rule.setOrder(section.getString(key + ".order", "ASCENDING"));
                    rule.setCaseSensitive(section.getBoolean(key + ".case-sensitive", false));
                } else if (type.equals("CUSTOM")) {
                    rule.setPlaceholder(section.getString(key + ".placeholder", ""));
                    rule.setCustomOrder(section.getStringList(key + ".order"));
                }
                
                rules.add(rule);
            }
        }
        return rules;
    }
    
    public boolean updateOnPermissionChange() {
        return config.getBoolean("update-on.permission-change", true);
    }
    
    public boolean updateOnGroupChange() {
        return config.getBoolean("update-on.group-change", true);
    }
    
    public boolean updateOnWorldChange() {
        return config.getBoolean("update-on.world-change", true);
    }
    
    public boolean updateOnPlaceholderChange() {
        return config.getBoolean("update-on.placeholder-change", true);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class SortingRule {
        private final String type;
        private final int priority;
        private Map<String, Integer> groupWeights;
        private String placeholder;
        private String order;
        private boolean caseSensitive;
        private List<String> customOrder;
        
        public SortingRule(String type, int priority) {
            this.type = type;
            this.priority = priority;
        }
        
        public String getType() { return type; }
        public int getPriority() { return priority; }
        public Map<String, Integer> getGroupWeights() { return groupWeights; }
        public String getPlaceholder() { return placeholder; }
        public String getOrder() { return order; }
        public boolean isCaseSensitive() { return caseSensitive; }
        public List<String> getCustomOrder() { return customOrder; }
        
        public void setGroupWeights(Map<String, Integer> groupWeights) { this.groupWeights = groupWeights; }
        public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
        public void setOrder(String order) { this.order = order; }
        public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }
        public void setCustomOrder(List<String> customOrder) { this.customOrder = customOrder; }
    }
}
