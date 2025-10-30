package online.demonzdevelopment.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.PlaceholderConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Manages custom placeholders and animations
 */
public class PlaceholderManager {
    
    private final DZTablist plugin;
    private final Map<String, Function<Player, String>> customPlaceholders = new HashMap<>();
    private final Map<String, BiFunction<Player, Player, String>> relationalPlaceholders = new HashMap<>();
    private final Map<String, Animation> animations = new HashMap<>();
    
    public PlaceholderManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize placeholder system
     */
    public void initialize() {
        loadAnimations();
        registerExpansion();
        plugin.getLogger().info("Placeholder manager initialized");
    }
    
    /**
     * Load animations from config
     */
    private void loadAnimations() {
        PlaceholderConfig config = plugin.getConfigManager().getPlaceholderConfig();
        for (Map.Entry<String, PlaceholderConfig.AnimationData> entry : config.getAnimations().entrySet()) {
            animations.put(entry.getKey(), new Animation(entry.getValue().getFrames(), entry.getValue().getInterval()));
        }
    }
    
    /**
     * Register PlaceholderAPI expansion
     */
    private void registerExpansion() {
        new DZTablistExpansion().register();
    }
    
    /**
     * Register a custom placeholder
     */
    public void registerPlaceholder(String identifier, Function<Player, String> function) {
        customPlaceholders.put(identifier, function);
    }
    
    /**
     * Register a relational placeholder
     */
    public void registerRelationalPlaceholder(String identifier, BiFunction<Player, Player, String> function) {
        relationalPlaceholders.put(identifier, function);
    }
    
    /**
     * Unregister a placeholder
     */
    public void unregisterPlaceholder(String identifier) {
        customPlaceholders.remove(identifier);
        relationalPlaceholders.remove(identifier);
    }
    
    /**
     * Replace placeholders in text
     */
    public String replace(Player player, String text) {
        if (text == null) return "";
        
        // Replace animations
        for (Map.Entry<String, Animation> entry : animations.entrySet()) {
            text = text.replace("%animation_" + entry.getKey() + "%", entry.getValue().getCurrentFrame());
        }
        
        // Replace custom placeholders
        for (Map.Entry<String, Function<Player, String>> entry : customPlaceholders.entrySet()) {
            text = text.replace("%placeholder_" + entry.getKey() + "%", entry.getValue().apply(player));
        }
        
        return text;
    }
    
    /**
     * Replace relational placeholders
     */
    public String replaceRelational(Player viewer, Player target, String text) {
        if (text == null) return "";
        
        for (Map.Entry<String, BiFunction<Player, Player, String>> entry : relationalPlaceholders.entrySet()) {
            text = text.replace("%rel_" + entry.getKey() + "%", entry.getValue().apply(viewer, target));
        }
        
        return text;
    }
    
    /**
     * Reload placeholder manager
     */
    public void reload() {
        animations.clear();
        loadAnimations();
    }
    
    /**
     * Shutdown placeholder manager
     */
    public void shutdown() {
        customPlaceholders.clear();
        relationalPlaceholders.clear();
        animations.clear();
    }
    
    /**
     * Animation class
     */
    private static class Animation {
        private final String[] frames;
        private final int interval;
        private int currentFrame = 0;
        private long lastUpdate = System.currentTimeMillis();
        
        public Animation(java.util.List<String> frames, int interval) {
            this.frames = frames.toArray(new String[0]);
            this.interval = interval;
        }
        
        public String getCurrentFrame() {
            if (System.currentTimeMillis() - lastUpdate >= interval) {
                currentFrame = (currentFrame + 1) % frames.length;
                lastUpdate = System.currentTimeMillis();
            }
            return frames[currentFrame];
        }
    }
    
    /**
     * PlaceholderAPI expansion
     */
    private class DZTablistExpansion extends PlaceholderExpansion {
        
        @Override
        public @NotNull String getIdentifier() {
            return "dztablist";
        }
        
        @Override
        public @NotNull String getAuthor() {
            return "DemonZ Development";
        }
        
        @Override
        public @NotNull String getVersion() {
            return plugin.getDescription().getVersion();
        }
        
        @Override
        public String onPlaceholderRequest(Player player, @NotNull String identifier) {
            if (player == null) return "";
            
            // Custom placeholder
            if (identifier.startsWith("custom_")) {
                String key = identifier.substring(7);
                Function<Player, String> function = customPlaceholders.get(key);
                return function != null ? function.apply(player) : "";
            }
            
            // Animation
            if (identifier.startsWith("animation_")) {
                String key = identifier.substring(10);
                Animation animation = animations.get(key);
                return animation != null ? animation.getCurrentFrame() : "";
            }
            
            return null;
        }
    }
}
