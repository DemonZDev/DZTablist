package online.demonzdevelopment.managers;

import net.kyori.adventure.text.Component;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.ScoreboardConfig;
import online.demonzdevelopment.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * Manages scoreboard display
 */
public class ScoreboardManager {
    
    private final DZTablist plugin;
    private final Map<String, ScoreboardData> scoreboards = new HashMap<>();
    private final Map<UUID, String> playerScoreboards = new HashMap<>();
    private BukkitTask updateTask;
    
    public ScoreboardManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize scoreboard system
     */
    public void initialize() {
        loadScoreboards();
        startUpdateTask();
        plugin.getLogger().info("Scoreboard manager initialized");
    }
    
    /**
     * Load scoreboards from config
     */
    private void loadScoreboards() {
        ScoreboardConfig config = plugin.getConfigManager().getScoreboardConfig();
        scoreboards.putAll(config.getScoreboards());
    }
    
    /**
     * Start update task
     */
    private void startUpdateTask() {
        int interval = plugin.getConfigManager().getMainConfig().getRefreshInterval("scoreboard");
        
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getPerformanceMonitor().startTiming("scoreboard");
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
            
            plugin.getPerformanceMonitor().stopTiming("scoreboard");
        }, 20L, interval / 50L);
    }
    
    /**
     * Update scoreboard for player
     */
    public void updateScoreboard(Player player) {
        if (!plugin.getDatabaseManager().isScoreboardEnabled(player.getUniqueId())) {
            return;
        }
        
        String scoreboardId = getScoreboardForPlayer(player);
        if (scoreboardId == null) return;
        
        ScoreboardConfig.ScoreboardData data = scoreboards.get(scoreboardId);
        if (data == null) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            showScoreboard(player, scoreboardId);
        });
    }
    
    /**
     * Get appropriate scoreboard for player
     */
    private String getScoreboardForPlayer(Player player) {
        ScoreboardConfig config = plugin.getConfigManager().getScoreboardConfig();
        
        // Check world assignment
        String world = player.getWorld().getName();
        if (config.getWorldAssignments().containsKey(world)) {
            return config.getWorldAssignments().get(world);
        }
        
        return "default";
    }
    
    /**
     * Show scoreboard to player
     */
    public void showScoreboard(Player player, String id) {
        ScoreboardConfig.ScoreboardData data = scoreboards.get(id);
        if (data == null) return;
        
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dztablist", Criteria.DUMMY, 
                ColorUtil.parseComponent(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, data.getTitle())));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        // Add lines
        List<String> lines = data.getLines();
        for (int i = 0; i < lines.size() && i < 15; i++) {
            String line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, lines.get(i));
            objective.getScore(ColorUtil.convertToLegacy(line)).setScore(lines.size() - i);
        }
        
        player.setScoreboard(scoreboard);
        playerScoreboards.put(player.getUniqueId(), id);
    }
    
    /**
     * Hide scoreboard from player
     */
    public void hideScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        playerScoreboards.remove(player.getUniqueId());
    }
    
    /**
     * Toggle scoreboard for player
     */
    public void toggleScoreboard(Player player) {
        boolean enabled = !plugin.getDatabaseManager().isScoreboardEnabled(player.getUniqueId());
        plugin.getDatabaseManager().saveToggle(player.getUniqueId(), "scoreboard", enabled);
        
        if (!enabled) {
            hideScoreboard(player);
        } else {
            updateScoreboard(player);
        }
    }
    
    /**
     * Reload manager
     */
    public void reload() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        scoreboards.clear();
        playerScoreboards.clear();
        loadScoreboards();
        startUpdateTask();
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            hideScoreboard(player);
        }
        
        scoreboards.clear();
        playerScoreboards.clear();
    }
}
