package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Placeholder configuration handler
 */
public class PlaceholderConfig {
    
    private final FileConfiguration config;
    
    public PlaceholderConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("placeholders");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                placeholders.put(key, section.getString(key + ".value", ""));
            }
        }
        return placeholders;
    }
    
    public Map<String, AnimationData> getAnimations() {
        Map<String, AnimationData> animations = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("animations");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> frames = section.getStringList(key + ".frames");
                int interval = section.getInt(key + ".interval", 500);
                animations.put(key, new AnimationData(frames, interval));
            }
        }
        return animations;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class AnimationData {
        private final List<String> frames;
        private final int interval;
        
        public AnimationData(List<String> frames, int interval) {
            this.frames = frames;
            this.interval = interval;
        }
        
        public List<String> getFrames() { return frames; }
        public int getInterval() { return interval; }
    }
}
