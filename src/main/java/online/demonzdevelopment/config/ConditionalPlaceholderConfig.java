package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conditional placeholder configuration handler
 */
public class ConditionalPlaceholderConfig {
    
    private final FileConfiguration config;
    
    public ConditionalPlaceholderConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public Map<String, ConditionalData> getConditionals() {
        Map<String, ConditionalData> conditionals = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("conditionals");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<Condition> conditions = new ArrayList<>();
                ConfigurationSection condSection = section.getConfigurationSection(key + ".conditions");
                if (condSection != null) {
                    for (String condKey : condSection.getKeys(false)) {
                        String condition = condSection.getString(condKey + ".condition", "");
                        String output = condSection.getString(condKey + ".output", "");
                        conditions.add(new Condition(condition, output));
                    }
                }
                String defaultOutput = section.getString(key + ".default", "");
                conditionals.put(key, new ConditionalData(conditions, defaultOutput));
            }
        }
        return conditionals;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class ConditionalData {
        private final List<Condition> conditions;
        private final String defaultOutput;
        
        public ConditionalData(List<Condition> conditions, String defaultOutput) {
            this.conditions = conditions;
            this.defaultOutput = defaultOutput;
        }
        
        public List<Condition> getConditions() { return conditions; }
        public String getDefaultOutput() { return defaultOutput; }
    }
    
    public static class Condition {
        private final String condition;
        private final String output;
        
        public Condition(String condition, String output) {
            this.condition = condition;
            this.output = output;
        }
        
        public String getCondition() { return condition; }
        public String getOutput() { return output; }
    }
}
