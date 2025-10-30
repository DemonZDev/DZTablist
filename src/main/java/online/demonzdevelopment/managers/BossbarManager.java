package online.demonzdevelopment.managers;

import net.kyori.adventure.bossbar.BossBar;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.BossbarConfig;
import online.demonzdevelopment.util.ColorUtil;
import online.demonzdevelopment.util.ConditionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages bossbar display
 */
public class BossbarManager {
    
    private final DZTablist plugin;
    private final Map<String, BossBar> bossbars = new HashMap<>();
    private final Map<UUID, Set<String>> playerBossbars = new HashMap<>();
    private BukkitTask updateTask;
    
    public BossbarManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize bossbar system
     */
    public void initialize() {
        loadBossbars();
        startUpdateTask();
        plugin.getLogger().info("Bossbar manager initialized");
    }
    
    /**
     * Load bossbars from config
     */
    private void loadBossbars() {
        BossbarConfig config = plugin.getConfigManager().getBossbarConfig();
        for (Map.Entry<String, BossbarConfig.BossbarData> entry : config.getBossbars().entrySet()) {
            BossbarConfig.BossbarData data = entry.getValue();
            BossBar bossbar = BossBar.bossBar(
                    ColorUtil.parseComponent(data.getText()),
                    1.0f,
                    getBossbarColor(data.getColor()),
                    getBossbarOverlay(data.getStyle())
            );
            bossbars.put(entry.getKey(), bossbar);
        }
    }
    
    /**
     * Start update task
     */
    private void startUpdateTask() {
        int interval = plugin.getConfigManager().getMainConfig().getRefreshInterval("bossbar");
        
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getPerformanceMonitor().startTiming("bossbar");
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateBossbars(player);
            }
            
            plugin.getPerformanceMonitor().stopTiming("bossbar");
        }, 20L, interval / 50L);
    }
    
    /**
     * Update bossbars for player
     */
    public void updateBossbars(Player player) {
        if (!plugin.getDatabaseManager().isBossbarEnabled(player.getUniqueId())) {
            return;
        }
        
        BossbarConfig config = plugin.getConfigManager().getBossbarConfig();
        
        for (Map.Entry<String, BossbarConfig.BossbarData> entry : config.getBossbars().entrySet()) {
            String id = entry.getKey();
            BossbarConfig.BossbarData data = entry.getValue();
            
            // Check conditions
            if (ConditionUtil.evaluateAll(player, data.getConditions())) {
                showBossbar(player, id);
            } else {
                hideBossbar(player, id);
            }
        }
    }
    
    /**
     * Show bossbar to player
     */
    public void showBossbar(Player player, String id) {
        BossBar bossbar = bossbars.get(id);
        if (bossbar == null) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.showBossBar(bossbar);
            playerBossbars.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(id);
        });
    }
    
    /**
     * Hide bossbar from player
     */
    public void hideBossbar(Player player, String id) {
        BossBar bossbar = bossbars.get(id);
        if (bossbar == null) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.hideBossBar(bossbar);
            Set<String> playerBars = playerBossbars.get(player.getUniqueId());
            if (playerBars != null) {
                playerBars.remove(id);
            }
        });
    }
    
    /**
     * Toggle bossbar for player
     */
    public void toggleBossbar(Player player) {
        boolean enabled = !plugin.getDatabaseManager().isBossbarEnabled(player.getUniqueId());
        plugin.getDatabaseManager().saveToggle(player.getUniqueId(), "bossbar", enabled);
        
        if (!enabled) {
            // Hide all bossbars
            Set<String> playerBars = playerBossbars.get(player.getUniqueId());
            if (playerBars != null) {
                for (String id : new HashSet<>(playerBars)) {
                    hideBossbar(player, id);
                }
            }
        } else {
            // Show applicable bossbars
            updateBossbars(player);
        }
    }
    
    /**
     * Get bossbar color
     */
    private BossBar.Color getBossbarColor(String color) {
        try {
            return BossBar.Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Color.GREEN;
        }
    }
    
    /**
     * Get bossbar overlay
     */
    private BossBar.Overlay getBossbarOverlay(String style) {
        switch (style.toUpperCase()) {
            case "SEGMENTED_6": return BossBar.Overlay.NOTCHED_6;
            case "SEGMENTED_10": return BossBar.Overlay.NOTCHED_10;
            case "SEGMENTED_12": return BossBar.Overlay.NOTCHED_12;
            case "SEGMENTED_20": return BossBar.Overlay.NOTCHED_20;
            default: return BossBar.Overlay.PROGRESS;
        }
    }
    
    /**
     * Reload manager
     */
    public void reload() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        bossbars.clear();
        playerBossbars.clear();
        loadBossbars();
        startUpdateTask();
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        
        // Hide all bossbars
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<String> playerBars = playerBossbars.get(player.getUniqueId());
            if (playerBars != null) {
                for (String id : playerBars) {
                    hideBossbar(player, id);
                }
            }
        }
        
        bossbars.clear();
        playerBossbars.clear();
    }
}
