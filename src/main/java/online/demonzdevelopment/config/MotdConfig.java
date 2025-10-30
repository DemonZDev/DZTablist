package online.demonzdevelopment.config;

import online.demonzdevelopment.DZTablist;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * Configuration manager for MOTD system
 * Handles loading and managing MOTD configurations with animations, conditions, and formatting
 */
public class MotdConfig {
    
    private final DZTablist plugin;
    private FileConfiguration config;
    private File configFile;
    
    // Settings
    private boolean enabled;
    private int maxPlayers;
    private String playerCountFormat;
    private boolean fakePlayerCountEnabled;
    private int fakePlayerCountAdd;
    private int fakePlayerCountFixed;
    
    // Color downsampling
    private boolean colorDownsamplingEnabled;
    private String colorDownsamplingMethod;
    private boolean versionSpecific;
    
    // Rotation
    private boolean rotationEnabled;
    private String rotationType;
    private int rotationInterval;
    
    // Animation
    private boolean animationEnabled;
    private int frameInterval;
    private String animationType;
    
    // MOTDs
    private final Map<String, MotdEntry> motds = new LinkedHashMap<>();
    
    public MotdConfig(DZTablist plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "motd.yml");
    }
    
    public void load() {
        if (!configFile.exists()) {
            plugin.saveResource("motd.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        loadSettings();
        loadMotds();
    }
    
    public void reload() {
        motds.clear();
        load();
    }
    
    private void loadSettings() {
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            enabled = settings.getBoolean("enabled", true);
            maxPlayers = settings.getInt("max-players", -1);
            playerCountFormat = settings.getString("player-count-format", "%online%/%max%");
            
            ConfigurationSection fakeCount = settings.getConfigurationSection("fake-player-count");
            if (fakeCount != null) {
                fakePlayerCountEnabled = fakeCount.getBoolean("enabled", false);
                fakePlayerCountAdd = fakeCount.getInt("add-players", 0);
                fakePlayerCountFixed = fakeCount.getInt("fixed-count", -1);
            }
        }
        
        ConfigurationSection colorDown = config.getConfigurationSection("color-downsampling");
        if (colorDown != null) {
            colorDownsamplingEnabled = colorDown.getBoolean("enabled", true);
            colorDownsamplingMethod = colorDown.getString("method", "closest-match");
            versionSpecific = colorDown.getBoolean("version-specific", true);
        }
        
        ConfigurationSection rotation = config.getConfigurationSection("rotation");
        if (rotation != null) {
            rotationEnabled = rotation.getBoolean("enabled", true);
            rotationType = rotation.getString("type", "priority");
            rotationInterval = rotation.getInt("interval", 300);
        }
        
        ConfigurationSection animation = config.getConfigurationSection("animation");
        if (animation != null) {
            animationEnabled = animation.getBoolean("enabled", true);
            frameInterval = animation.getInt("frame-interval", 200);
            animationType = animation.getString("type", "cycle");
        }
    }
    
    private void loadMotds() {
        ConfigurationSection motdsSection = config.getConfigurationSection("motds");
        if (motdsSection == null) return;
        
        for (String key : motdsSection.getKeys(false)) {
            ConfigurationSection motdSection = motdsSection.getConfigurationSection(key);
            if (motdSection == null) continue;
            
            MotdEntry entry = new MotdEntry();
            entry.setKey(key);
            entry.setEnabled(motdSection.getBoolean("enabled", true));
            entry.setPriority(motdSection.getInt("priority", 1));
            entry.setAnimated(motdSection.getBoolean("animated", false));
            
            if (entry.isAnimated() && motdSection.contains("frames")) {
                List<Map<?, ?>> framesList = motdSection.getMapList("frames");
                for (Map<?, ?> frameMap : framesList) {
                    String line1 = (String) frameMap.get("line1");
                    String line2 = (String) frameMap.get("line2");
                    if (line1 != null && line2 != null) {
                        entry.addFrame(new MotdFrame(line1, line2));
                    }
                }
            } else {
                String line1 = motdSection.getString("line1", "");
                String line2 = motdSection.getString("line2", "");
                entry.setLine1(line1);
                entry.setLine2(line2);
            }
            
            if (motdSection.contains("conditions")) {
                List<String> conditions = motdSection.getStringList("conditions");
                entry.setConditions(conditions);
            }
            
            motds.put(key, entry);
        }
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getPlayerCountFormat() { return playerCountFormat; }
    public boolean isFakePlayerCountEnabled() { return fakePlayerCountEnabled; }
    public int getFakePlayerCountAdd() { return fakePlayerCountAdd; }
    public int getFakePlayerCountFixed() { return fakePlayerCountFixed; }
    public boolean isColorDownsamplingEnabled() { return colorDownsamplingEnabled; }
    public String getColorDownsamplingMethod() { return colorDownsamplingMethod; }
    public boolean isVersionSpecific() { return versionSpecific; }
    public boolean isRotationEnabled() { return rotationEnabled; }
    public String getRotationType() { return rotationType; }
    public int getRotationInterval() { return rotationInterval; }
    public boolean isAnimationEnabled() { return animationEnabled; }
    public int getFrameInterval() { return frameInterval; }
    public String getAnimationType() { return animationType; }
    public Map<String, MotdEntry> getMotds() { return motds; }
    
    /**
     * MOTD Entry representing a single MOTD configuration
     */
    public static class MotdEntry {
        private String key;
        private boolean enabled;
        private String line1;
        private String line2;
        private int priority;
        private List<String> conditions = new ArrayList<>();
        private boolean animated;
        private List<MotdFrame> frames = new ArrayList<>();
        private int currentFrame = 0;
        
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }
        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
        public boolean isAnimated() { return animated; }
        public void setAnimated(boolean animated) { this.animated = animated; }
        public List<MotdFrame> getFrames() { return frames; }
        public void addFrame(MotdFrame frame) { this.frames.add(frame); }
        public int getCurrentFrame() { return currentFrame; }
        public void setCurrentFrame(int currentFrame) { this.currentFrame = currentFrame; }
        
        public MotdFrame getNextFrame() {
            if (frames.isEmpty()) return null;
            currentFrame = (currentFrame + 1) % frames.size();
            return frames.get(currentFrame);
        }
        
        public MotdFrame getCurrentFrameData() {
            if (frames.isEmpty()) return null;
            return frames.get(currentFrame);
        }
    }
    
    /**
     * MOTD Frame for animated MOTDs
     */
    public static class MotdFrame {
        private final String line1;
        private final String line2;
        
        public MotdFrame(String line1, String line2) {
            this.line1 = line1;
            this.line2 = line2;
        }
        
        public String getLine1() { return line1; }
        public String getLine2() { return line2; }
    }
}
