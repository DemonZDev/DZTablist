package online.demonzdevelopment.managers;

import net.kyori.adventure.text.format.NamedTextColor;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.NameTagConfig;
import online.demonzdevelopment.utils.ColorUtil;
import online.demonzdevelopment.utils.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player nametags
 */
public class NameTagManager {
    
    private final DZTablist plugin;
    private BukkitTask updateTask;
    private final Map<UUID, PlayerNameTag> cache = new HashMap<>();
    
    public NameTagManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize nametag system
     */
    public void initialize() {
        startUpdateTask();
        plugin.getLogger().info("NameTag manager initialized");
    }
    
    /**
     * Start update task
     */
    private void startUpdateTask() {
        int interval = plugin.getConfigManager().getMainConfig().getRefreshInterval("nametags");
        
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getPerformanceMonitor().startTiming("nametags");
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateNameTag(player);
            }
            
            plugin.getPerformanceMonitor().stopTiming("nametags");
        }, 20L, interval / 50L);
    }
    
    /**
     * Update nametag for a player
     */
    public void updateNameTag(Player player) {
        try {
            NameTagConfig config = plugin.getConfigManager().getNameTagConfig();
            
            // Get prefix and suffix
            String prefix = getPrefix(player, config);
            String suffix = getSuffix(player, config);
            
            // Apply to scoreboard team
            Bukkit.getScheduler().runTask(plugin, () -> {
                applyNameTag(player, prefix, suffix);
            });
            
            // Cache
            cache.put(player.getUniqueId(), new PlayerNameTag(prefix, suffix));
            
        } catch (Exception e) {
            plugin.getErrorLogger().log("Failed to update nametag for " + player.getName(), e);
        }
    }
    
    /**
     * Get prefix for player
     */
    private String getPrefix(Player player, NameTagConfig config) {
        String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                .getPrimaryGroup();
        NameTagConfig.NameTagData data = config.getGroupConfigs().get(group);
        if (data != null) {
            return PlaceholderUtil.replacePlaceholders(player, data.getPrefix());
        }
        return PlaceholderUtil.replacePlaceholders(player, config.getGlobalPrefix());
    }
    
    /**
     * Get suffix for player
     */
    private String getSuffix(Player player, NameTagConfig config) {
        String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                .getPrimaryGroup();
        NameTagConfig.NameTagData data = config.getGroupConfigs().get(group);
        if (data != null) {
            return PlaceholderUtil.replacePlaceholders(player, data.getSuffix());
        }
        return PlaceholderUtil.replacePlaceholders(player, config.getGlobalSuffix());
    }
    
    /**
     * Apply nametag using scoreboard teams
     */
    private void applyNameTag(Player player, String prefix, String suffix) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        String teamName = "dzt_" + player.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        // Convert to legacy format for nametags
        String legacyPrefix = ColorUtil.convertToLegacy(prefix);
        String legacySuffix = ColorUtil.convertToLegacy(suffix);
        
        // Limit to 16 characters for prefix/suffix (Minecraft limitation)
        if (legacyPrefix.length() > 16) legacyPrefix = legacyPrefix.substring(0, 16);
        if (legacySuffix.length() > 16) legacySuffix = legacySuffix.substring(0, 16);
        
        team.prefix(ColorUtil.parseComponent(legacyPrefix));
        team.suffix(ColorUtil.parseComponent(legacySuffix));
        
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }
    
    /**
     * Set custom prefix for player
     */
    public void setPrefix(Player player, String prefix) {
        String suffix = cache.containsKey(player.getUniqueId()) ? 
                cache.get(player.getUniqueId()).suffix : "";
        applyNameTag(player, prefix, suffix);
        cache.put(player.getUniqueId(), new PlayerNameTag(prefix, suffix));
    }
    
    /**
     * Set custom suffix for player
     */
    public void setSuffix(Player player, String suffix) {
        String prefix = cache.containsKey(player.getUniqueId()) ? 
                cache.get(player.getUniqueId()).prefix : "";
        applyNameTag(player, prefix, suffix);
        cache.put(player.getUniqueId(), new PlayerNameTag(prefix, suffix));
    }
    
    /**
     * Reload manager
     */
    public void reload() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        cache.clear();
        startUpdateTask();
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        cache.clear();
    }
    
    /**
     * Player nametag data
     */
    private static class PlayerNameTag {
        private final String prefix;
        private final String suffix;
        
        public PlayerNameTag(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}
