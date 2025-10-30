package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bossbar configuration handler
 */
public class BossbarConfig {
    
    private final FileConfiguration config;
    
    public BossbarConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public Map<String, BossbarData> getBossbars() {
        Map<String, BossbarData> bossbars = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("bossbars");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String text = section.getString(key + ".text", "");
                String progress = section.getString(key + ".progress", "1.0");
                String color = section.getString(key + ".color", "GREEN");
                String style = section.getString(key + ".style", "SOLID");
                List<String> conditions = section.getStringList(key + ".conditions");
                int refresh = section.getInt(key + ".refresh", 500);
                int duration = section.getInt(key + ".duration", 0);
                
                bossbars.put(key, new BossbarData(text, progress, color, style, conditions, refresh, duration));
            }
        }
        return bossbars;
    }
    
    public String getToggleCommand() {
        return config.getString("toggle.command", "/bossbar toggle");
    }
    
    public String getTogglePermission() {
        return config.getString("toggle.permission", "dztablist.bossbar.toggle");
    }
    
    public String getEnabledMessage() {
        return config.getString("toggle.messages.enabled", "&aBossbar enabled!");
    }
    
    public String getDisabledMessage() {
        return config.getString("toggle.messages.disabled", "&cBossbar disabled!");
    }
    
    public boolean isRememberChoice() {
        return config.getBoolean("toggle.remember-choice", true);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class BossbarData {
        private final String text;
        private final String progress;
        private final String color;
        private final String style;
        private final List<String> conditions;
        private final int refresh;
        private final int duration;
        
        public BossbarData(String text, String progress, String color, String style, List<String> conditions, int refresh, int duration) {
            this.text = text;
            this.progress = progress;
            this.color = color;
            this.style = style;
            this.conditions = conditions;
            this.refresh = refresh;
            this.duration = duration;
        }
        
        public String getText() { return text; }
        public String getProgress() { return progress; }
        public String getColor() { return color; }
        public String getStyle() { return style; }
        public List<String> getConditions() { return conditions; }
        public int getRefresh() { return refresh; }
        public int getDuration() { return duration; }
    }
}
