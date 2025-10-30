package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scoreboard configuration handler
 */
public class ScoreboardConfig {
    
    private final FileConfiguration config;
    
    public ScoreboardConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public Map<String, ScoreboardData> getScoreboards() {
        Map<String, ScoreboardData> scoreboards = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("scoreboards");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String title = section.getString(key + ".title", "");
                List<String> lines = section.getStringList(key + ".lines");
                int refresh = section.getInt(key + ".refresh", 1000);
                boolean showNumbers = section.getBoolean(key + ".show-numbers", false);
                int customNumber = section.getInt(key + ".custom-number", 0);
                List<String> rightSideText = section.getStringList(key + ".right-side-text");
                
                scoreboards.put(key, new ScoreboardData(title, lines, refresh, showNumbers, customNumber, rightSideText));
            }
        }
        return scoreboards;
    }
    
    public Map<String, String> getWorldAssignments() {
        Map<String, String> worlds = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("worlds");
        if (section != null) {
            for (String world : section.getKeys(false)) {
                worlds.put(world, section.getString(world + ".scoreboard", "default"));
            }
        }
        return worlds;
    }
    
    public String getToggleCommand() {
        return config.getString("toggle.command", "/scoreboard toggle");
    }
    
    public String getTogglePermission() {
        return config.getString("toggle.permission", "dztablist.scoreboard.toggle");
    }
    
    public String getEnabledMessage() {
        return config.getString("toggle.messages.enabled", "&aScoreboard enabled!");
    }
    
    public String getDisabledMessage() {
        return config.getString("toggle.messages.disabled", "&cScoreboard disabled!");
    }
    
    public boolean isRememberChoice() {
        return config.getBoolean("toggle.remember-choice", true);
    }
    
    public boolean isAutoDetection() {
        return config.getBoolean("auto-detection.other-plugins", true);
    }
    
    public String getAutoDetectionPriority() {
        return config.getString("auto-detection.priority", "LOW");
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class ScoreboardData {
        private final String title;
        private final List<String> lines;
        private final int refresh;
        private final boolean showNumbers;
        private final int customNumber;
        private final List<String> rightSideText;
        
        public ScoreboardData(String title, List<String> lines, int refresh, boolean showNumbers, int customNumber, List<String> rightSideText) {
            this.title = title;
            this.lines = lines;
            this.refresh = refresh;
            this.showNumbers = showNumbers;
            this.customNumber = customNumber;
            this.rightSideText = rightSideText;
        }
        
        public String getTitle() { return title; }
        public List<String> getLines() { return lines; }
        public int getRefresh() { return refresh; }
        public boolean isShowNumbers() { return showNumbers; }
        public int getCustomNumber() { return customNumber; }
        public List<String> getRightSideText() { return rightSideText; }
    }
}
