package online.demonzdevelopment.managers;

import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.SortingConfig;
import online.demonzdevelopment.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages player sorting in tablist
 */
public class SortingManager {
    
    private final DZTablist plugin;
    private final Map<UUID, Integer> customPriorities = new HashMap<>();
    
    public SortingManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize sorting system
     */
    public void initialize() {
        plugin.getLogger().info("Sorting manager initialized");
    }
    
    /**
     * Get sorting priority for player
     */
    public int getSortingPriority(Player player) {
        // Check custom priority
        if (customPriorities.containsKey(player.getUniqueId())) {
            return customPriorities.get(player.getUniqueId());
        }
        
        // Calculate priority from rules
        SortingConfig config = plugin.getConfigManager().getSortingConfig();
        int totalPriority = 0;
        
        for (SortingConfig.SortingRule rule : config.getSortingRules()) {
            if (rule.getType().equals("GROUP")) {
                String group = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId())
                        .getPrimaryGroup();
                Integer weight = rule.getGroupWeights().get(group);
                if (weight != null) {
                    totalPriority += weight * 1000; // Multiply for priority separation
                }
            }
        }
        
        return totalPriority;
    }
    
    /**
     * Set custom sorting priority
     */
    public void setSortingPriority(Player player, int priority) {
        customPriorities.put(player.getUniqueId(), priority);
    }
    
    /**
     * Update sorting for player
     */
    public void updateSorting(Player player) {
        // This would trigger tablist update
        if (plugin.getTablistFormatManager() != null) {
            plugin.getTablistFormatManager().updateTablist(player);
        }
    }
    
    /**
     * Compare two players for sorting
     */
    public int compare(Player p1, Player p2) {
        int priority1 = getSortingPriority(p1);
        int priority2 = getSortingPriority(p2);
        
        if (priority1 != priority2) {
            return Integer.compare(priority2, priority1); // Higher priority first
        }
        
        // Secondary sort by name
        return p1.getName().compareToIgnoreCase(p2.getName());
    }
    
    /**
     * Get sorted player list
     */
    public List<Player> getSortedPlayers() {
        List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        players.sort(this::compare);
        return players;
    }
    
    /**
     * Reload manager
     */
    public void reload() {
        customPriorities.clear();
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        customPriorities.clear();
    }
}
