package online.demonzdevelopment.api;

import net.kyori.adventure.bossbar.BossBar;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.util.PerformanceMonitor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Public API for DZTablist
 * Allows other plugins to interact with DZTablist features
 * 
 * @author DemonZ Development
 * @version 1.0.0
 */
public class DZTablistAPI {
    
    private static DZTablistAPI instance;
    private final DZTablist plugin;
    
    public DZTablistAPI(DZTablist plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    /**
     * Get API instance
     * 
     * @return API instance
     */
    public static DZTablistAPI getInstance() {
        return instance;
    }
    
    // ==================== Header & Footer ====================
    
    /**
     * Set custom header for a player
     * 
     * @param player The player
     * @param header Header text (supports MiniMessage format)
     */
    public void setHeader(Player player, String header) {
        if (plugin.getHeaderFooterManager() != null) {
            plugin.getHeaderFooterManager().setHeader(player, header);
        }
    }
    
    /**
     * Set custom footer for a player
     * 
     * @param player The player
     * @param footer Footer text (supports MiniMessage format)
     */
    public void setFooter(Player player, String footer) {
        if (plugin.getHeaderFooterManager() != null) {
            plugin.getHeaderFooterManager().setFooter(player, footer);
        }
    }
    
    // ==================== Nametags ====================
    
    /**
     * Set nametag prefix for a player
     * 
     * @param player The player
     * @param prefix Prefix text (supports color codes)
     */
    public void setNameTagPrefix(Player player, String prefix) {
        if (plugin.getNameTagManager() != null) {
            plugin.getNameTagManager().setPrefix(player, prefix);
        }
    }
    
    /**
     * Set nametag suffix for a player
     * 
     * @param player The player
     * @param suffix Suffix text (supports color codes)
     */
    public void setNameTagSuffix(Player player, String suffix) {
        if (plugin.getNameTagManager() != null) {
            plugin.getNameTagManager().setSuffix(player, suffix);
        }
    }
    
    /**
     * Set nametag visibility for a player
     * 
     * @param player The player
     * @param visibility Visibility option
     */
    public void setNameTagVisibility(Player player, NameTagVisibility visibility) {
        // Implementation would set visibility option
    }
    
    /**
     * Set nametag collision rule for a player
     * 
     * @param player The player
     * @param rule Collision rule
     */
    public void setNameTagCollisionRule(Player player, CollisionRule rule) {
        // Implementation would set collision rule
    }
    
    // ==================== Tablist Format ====================
    
    /**
     * Set tablist prefix for a player
     * 
     * @param player The player
     * @param prefix Prefix text
     */
    public void setTablistPrefix(Player player, String prefix) {
        // Implementation would set tablist prefix
    }
    
    /**
     * Set tablist name for a player
     * 
     * @param player The player
     * @param name Display name
     */
    public void setTablistName(Player player, String name) {
        // Implementation would set tablist name
    }
    
    /**
     * Set tablist suffix for a player
     * 
     * @param player The player
     * @param suffix Suffix text
     */
    public void setTablistSuffix(Player player, String suffix) {
        // Implementation would set tablist suffix
    }
    
    // ==================== Sorting ====================
    
    /**
     * Set custom sorting priority for a player
     * Higher priority = appears first in tablist
     * 
     * @param player The player
     * @param priority Priority value (higher = first)
     */
    public void setSortingPriority(Player player, int priority) {
        if (plugin.getSortingManager() != null) {
            plugin.getSortingManager().setSortingPriority(player, priority);
        }
    }
    
    /**
     * Update sorting for a player
     * Call this after changing sorting-related data
     * 
     * @param player The player
     */
    public void updateSorting(Player player) {
        if (plugin.getSortingManager() != null) {
            plugin.getSortingManager().updateSorting(player);
        }
    }
    
    // ==================== Scoreboard ====================
    
    /**
     * Create a custom scoreboard
     * 
     * @param id Scoreboard identifier
     * @param title Scoreboard title (supports MiniMessage)
     * @param lines Scoreboard lines (supports placeholders)
     */
    public void createScoreboard(String id, String title, List<String> lines) {
        // Implementation would create custom scoreboard
    }
    
    /**
     * Show a scoreboard to a player
     * 
     * @param player The player
     * @param id Scoreboard identifier
     */
    public void showScoreboard(Player player, String id) {
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().showScoreboard(player, id);
        }
    }
    
    /**
     * Hide scoreboard from a player
     * 
     * @param player The player
     */
    public void hideScoreboard(Player player) {
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().hideScoreboard(player);
        }
    }
    
    /**
     * Toggle scoreboard for a player
     * 
     * @param player The player
     */
    public void toggleScoreboard(Player player) {
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().toggleScoreboard(player);
        }
    }
    
    /**
     * Announce a scoreboard to a player for X seconds
     * 
     * @param player The player
     * @param id Scoreboard identifier
     * @param seconds Duration in seconds
     */
    public void announceScoreboard(Player player, String id, int seconds) {
        showScoreboard(player, id);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            hideScoreboard(player);
        }, seconds * 20L);
    }
    
    // ==================== Bossbar ====================
    
    /**
     * Create a custom bossbar
     * 
     * @param id Bossbar identifier
     * @param text Bossbar text (supports MiniMessage)
     * @param progress Progress value (0.0 - 1.0)
     * @param color Bossbar color
     * @param style Bossbar style
     */
    public void createBossbar(String id, String text, float progress, BossbarColor color, BossbarStyle style) {
        // Implementation would create custom bossbar
    }
    
    /**
     * Show a bossbar to a player
     * 
     * @param player The player
     * @param id Bossbar identifier
     */
    public void showBossbar(Player player, String id) {
        if (plugin.getBossbarManager() != null) {
            plugin.getBossbarManager().showBossbar(player, id);
        }
    }
    
    /**
     * Hide a bossbar from a player
     * 
     * @param player The player
     * @param id Bossbar identifier
     */
    public void hideBossbar(Player player, String id) {
        if (plugin.getBossbarManager() != null) {
            plugin.getBossbarManager().hideBossbar(player, id);
        }
    }
    
    /**
     * Toggle all bossbars for a player
     * 
     * @param player The player
     */
    public void toggleBossbar(Player player) {
        if (plugin.getBossbarManager() != null) {
            plugin.getBossbarManager().toggleBossbar(player);
        }
    }
    
    /**
     * Announce a bossbar to a player for X seconds
     * 
     * @param player The player
     * @param id Bossbar identifier
     * @param seconds Duration in seconds
     */
    public void announceBossbar(Player player, String id, int seconds) {
        showBossbar(player, id);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            hideBossbar(player, id);
        }, seconds * 20L);
    }
    
    /**
     * Update bossbar progress
     * 
     * @param id Bossbar identifier
     * @param progress New progress (0.0 - 1.0)
     */
    public void updateBossbarProgress(String id, float progress) {
        // Implementation would update bossbar progress
    }
    
    /**
     * Update bossbar text
     * 
     * @param id Bossbar identifier
     * @param text New text
     */
    public void updateBossbarText(String id, String text) {
        // Implementation would update bossbar text
    }
    
    // ==================== Layout ====================
    
    /**
     * Create a custom tablist layout
     * 
     * @param id Layout identifier
     * @param slots Slot configuration map (slot index -> slot config)
     */
    public void createLayout(String id, Map<Integer, SlotConfig> slots) {
        // Implementation would create custom layout
    }
    
    /**
     * Send a layout to a player
     * 
     * @param player The player
     * @param id Layout identifier
     */
    public void sendLayout(Player player, String id) {
        // Implementation would send layout to player
    }
    
    // ==================== Placeholders ====================
    
    /**
     * Register a custom placeholder
     * 
     * @param identifier Placeholder identifier (use without %)
     * @param refreshInterval Refresh interval in milliseconds
     * @param function Function to get placeholder value
     */
    public void registerPlaceholder(String identifier, int refreshInterval, Function<Player, String> function) {
        if (plugin.getPlaceholderManager() != null) {
            plugin.getPlaceholderManager().registerPlaceholder(identifier, function);
        }
    }
    
    /**
     * Register a relational placeholder
     * Relational placeholders take two players (viewer and target)
     * 
     * @param identifier Placeholder identifier (use without %)
     * @param refreshInterval Refresh interval in milliseconds
     * @param function Function to get placeholder value (viewer, target) -> value
     */
    public void registerRelationalPlaceholder(String identifier, int refreshInterval, BiFunction<Player, Player, String> function) {
        if (plugin.getPlaceholderManager() != null) {
            plugin.getPlaceholderManager().registerRelationalPlaceholder(identifier, function);
        }
    }
    
    /**
     * Unregister a placeholder
     * 
     * @param identifier Placeholder identifier
     */
    public void unregisterPlaceholder(String identifier) {
        if (plugin.getPlaceholderManager() != null) {
            plugin.getPlaceholderManager().unregisterPlaceholder(identifier);
        }
    }
    
    // ==================== Utility ====================
    
    /**
     * Reload the plugin
     */
    public void reload() {
        plugin.reload();
    }
    
    /**
     * Get performance statistics for all features
     * 
     * @return Map of feature name to performance stats
     */
    public Map<String, PerformanceStats> getPerformanceStats() {
        Map<String, PerformanceStats> stats = new HashMap<>();
        
        for (Map.Entry<String, PerformanceMonitor.FeatureStats> entry : 
                plugin.getPerformanceMonitor().getAllStats().entrySet()) {
            PerformanceMonitor.FeatureStats featureStats = entry.getValue();
            stats.put(entry.getKey(), new PerformanceStats(
                    featureStats.getCalls(),
                    featureStats.getTotalTime(),
                    featureStats.getAverageTime()
            ));
        }
        
        return stats;
    }
    
    // ==================== Inner Classes ====================
    
    /**
     * Slot configuration for custom layouts
     */
    public static class SlotConfig {
        private final SlotType type;
        private final String text;
        private final String skinUUID;
        
        public SlotConfig(SlotType type, String text, String skinUUID) {
            this.type = type;
            this.text = text;
            this.skinUUID = skinUUID;
        }
        
        public SlotType getType() { return type; }
        public String getText() { return text; }
        public String getSkinUUID() { return skinUUID; }
    }
    
    /**
     * Slot types for layouts
     */
    public enum SlotType {
        STATIC,
        PLAYER_GROUP,
        DYNAMIC
    }
    
    /**
     * Performance statistics
     */
    public static class PerformanceStats {
        private final long calls;
        private final double totalTime;
        private final double averageTime;
        
        public PerformanceStats(long calls, double totalTime, double averageTime) {
            this.calls = calls;
            this.totalTime = totalTime;
            this.averageTime = averageTime;
        }
        
        public long getCalls() { return calls; }
        public double getTotalTime() { return totalTime; }
        public double getAverageTime() { return averageTime; }
    }
}
