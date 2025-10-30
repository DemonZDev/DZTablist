package online.demonzdevelopment.managers;

import net.kyori.adventure.text.Component;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.HeaderFooterConfig;
import online.demonzdevelopment.util.ColorUtil;
import online.demonzdevelopment.util.ConditionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages header and footer display
 */
public class HeaderFooterManager {
    
    private final DZTablist plugin;
    private BukkitTask updateTask;
    private final Map<UUID, CachedHeaderFooter> cache = new HashMap<>();
    
    public HeaderFooterManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize header/footer system
     */
    public void initialize() {
        startUpdateTask();
        plugin.getLogger().info("Header/Footer manager initialized");
    }
    
    /**
     * Start update task
     */
    private void startUpdateTask() {
        int interval = plugin.getConfigManager().getMainConfig().getRefreshInterval("header-footer");
        
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getPerformanceMonitor().startTiming("header-footer");
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateHeaderFooter(player);
            }
            
            plugin.getPerformanceMonitor().stopTiming("header-footer");
        }, 20L, interval / 50L);
    }
    
    /**
     * Update header and footer for a player
     */
    public void updateHeaderFooter(Player player) {
        try {
            HeaderFooterConfig config = plugin.getConfigManager().getHeaderFooterConfig();
            
            // Get appropriate header/footer based on priority
            List<String> header = getHeader(player, config);
            List<String> footer = getFooter(player, config);
            
            // Convert to components
            Component headerComponent = parseLines(player, header);
            Component footerComponent = parseLines(player, footer);
            
            // Send to player
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendPlayerListHeaderAndFooter(headerComponent, footerComponent);
            });
            
            // Cache
            cache.put(player.getUniqueId(), new CachedHeaderFooter(headerComponent, footerComponent));
            
        } catch (Exception e) {
            plugin.getErrorLogger().log("Failed to update header/footer for " + player.getName(), e);
        }
    }
    
    /**
     * Get header for player based on priority
     */
    private List<String> getHeader(Player player, HeaderFooterConfig config) {
        // Priority: Player > Conditional > Group > World > Global
        
        // Check player-specific
        String uuid = player.getUniqueId().toString();
        HeaderFooterConfig.HeaderFooterData playerData = config.getPlayerConfigs().get(uuid);
        if (playerData != null && !playerData.getHeader().isEmpty()) {
            return playerData.getHeader();
        }
        
        // Check conditionals
        for (HeaderFooterConfig.ConditionalHeaderFooter conditional : config.getConditionalConfigs()) {
            if (ConditionUtil.evaluate(player, conditional.getCondition())) {
                if (!conditional.getHeader().isEmpty()) {
                    return conditional.getHeader();
                }
            }
        }
        
        // Check group
        String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                .getPrimaryGroup();
        HeaderFooterConfig.HeaderFooterData groupData = config.getGroupConfigs().get(group);
        if (groupData != null && !groupData.getHeader().isEmpty()) {
            return groupData.getHeader();
        }
        
        // Check world
        String world = player.getWorld().getName();
        HeaderFooterConfig.HeaderFooterData worldData = config.getWorldConfigs().get(world);
        if (worldData != null && !worldData.getHeader().isEmpty()) {
            return worldData.getHeader();
        }
        
        // Default to global
        return config.getGlobalHeader();
    }
    
    /**
     * Get footer for player based on priority
     */
    private List<String> getFooter(Player player, HeaderFooterConfig config) {
        // Same priority system as header
        
        String uuid = player.getUniqueId().toString();
        HeaderFooterConfig.HeaderFooterData playerData = config.getPlayerConfigs().get(uuid);
        if (playerData != null && !playerData.getFooter().isEmpty()) {
            return playerData.getFooter();
        }
        
        for (HeaderFooterConfig.ConditionalHeaderFooter conditional : config.getConditionalConfigs()) {
            if (ConditionUtil.evaluate(player, conditional.getCondition())) {
                if (!conditional.getFooter().isEmpty()) {
                    return conditional.getFooter();
                }
            }
        }
        
        String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                .getPrimaryGroup();
        HeaderFooterConfig.HeaderFooterData groupData = config.getGroupConfigs().get(group);
        if (groupData != null && !groupData.getFooter().isEmpty()) {
            return groupData.getFooter();
        }
        
        String world = player.getWorld().getName();
        HeaderFooterConfig.HeaderFooterData worldData = config.getWorldConfigs().get(world);
        if (worldData != null && !worldData.getFooter().isEmpty()) {
            return worldData.getFooter();
        }
        
        return config.getGlobalFooter();
    }
    
    /**
     * Parse lines to component
     */
    private Component parseLines(Player player, List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Component.empty();
        }
        
        Component result = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            String line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, lines.get(i));
            result = result.append(ColorUtil.parseComponent(line));
            if (i < lines.size() - 1) {
                result = result.append(Component.newline());
            }
        }
        return result;
    }
    
    /**
     * Set custom header for player
     */
    public void setHeader(Player player, String header) {
        Component component = ColorUtil.parseComponent(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, header));
        Component footer = cache.containsKey(player.getUniqueId()) ? 
                cache.get(player.getUniqueId()).footer : Component.empty();
        player.sendPlayerListHeaderAndFooter(component, footer);
        cache.put(player.getUniqueId(), new CachedHeaderFooter(component, footer));
    }
    
    /**
     * Set custom footer for player
     */
    public void setFooter(Player player, String footer) {
        Component component = ColorUtil.parseComponent(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, footer));
        Component header = cache.containsKey(player.getUniqueId()) ? 
                cache.get(player.getUniqueId()).header : Component.empty();
        player.sendPlayerListHeaderAndFooter(header, component);
        cache.put(player.getUniqueId(), new CachedHeaderFooter(header, component));
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
     * Cached header/footer
     */
    private static class CachedHeaderFooter {
        private final Component header;
        private final Component footer;
        
        public CachedHeaderFooter(Component header, Component footer) {
            this.header = header;
            this.footer = footer;
        }
    }
}
