package online.demonzdevelopment.managers;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.MotdConfig;
import online.demonzdevelopment.util.ColorUtil;
import online.demonzdevelopment.util.ConditionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages MOTD display and animations
 * Handles conditional MOTDs, color downsampling, and dynamic player count
 */
public class MotdManager implements Listener {
    
    private final DZTablist plugin;
    private final MotdConfig config;
    private final MiniMessage miniMessage;
    private BukkitTask animationTask;
    private int rotationIndex = 0;
    private final Random random = new Random();
    
    public MotdManager(DZTablist plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getMotdConfig();
        this.miniMessage = MiniMessage.miniMessage();
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startAnimationTask();
    }
    
    /**
     * Start the animation task for animated MOTDs
     */
    private void startAnimationTask() {
        if (!config.isAnimationEnabled()) return;
        
        int interval = config.getFrameInterval() / 50; // Convert ms to ticks
        if (interval < 1) interval = 1;
        
        animationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (MotdConfig.MotdEntry entry : config.getMotds().values()) {
                if (entry.isEnabled() && entry.isAnimated()) {
                    entry.getNextFrame();
                }
            }
        }, interval, interval);
    }
    
    /**
     * Stop the animation task
     */
    public void shutdown() {
        if (animationTask != null) {
            animationTask.cancel();
        }
    }
    
    /**
     * Reload the MOTD configuration
     */
    public void reload() {
        shutdown();
        config.reload();
        startAnimationTask();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!config.isEnabled()) return;
        
        try {
            // Get the best matching MOTD
            MotdConfig.MotdEntry motd = getBestMotd(event);
            if (motd == null) return;
            
            // Get MOTD lines
            String line1, line2;
            if (motd.isAnimated() && config.isAnimationEnabled()) {
                MotdConfig.MotdFrame frame = motd.getCurrentFrameData();
                if (frame != null) {
                    line1 = frame.getLine1();
                    line2 = frame.getLine2();
                } else {
                    line1 = motd.getLine1();
                    line2 = motd.getLine2();
                }
            } else {
                line1 = motd.getLine1();
                line2 = motd.getLine2();
            }
            
            // Process placeholders
            line1 = processPlaceholders(line1);
            line2 = processPlaceholders(line2);
            
            // Handle color downsampling for legacy clients
            int clientVersion = event.getClient().getProtocolVersion();
            if (config.isColorDownsamplingEnabled() && clientVersion < 735) { // 735 = 1.16
                line1 = downsampleColors(line1);
                line2 = downsampleColors(line2);
            }
            
            // Parse and set MOTD
            Component motdComponent = miniMessage.deserialize(line1 + "
" + line2);
            event.motd(motdComponent);
            
            // Handle player count modifications
            handlePlayerCount(event);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error setting MOTD: " + e.getMessage());
        }
    }
    
    /**
     * Get the best matching MOTD based on conditions and rotation type
     */
    private MotdConfig.MotdEntry getBestMotd(PaperServerListPingEvent event) {
        List<MotdConfig.MotdEntry> validMotds = config.getMotds().values().stream()
                .filter(MotdConfig.MotdEntry::isEnabled)
                .filter(motd -> checkConditions(motd, event))
                .collect(Collectors.toList());
        
        if (validMotds.isEmpty()) return null;
        
        String rotationType = config.getRotationType();
        
        switch (rotationType.toLowerCase()) {
            case "priority":
                return validMotds.stream()
                        .max(Comparator.comparingInt(MotdConfig.MotdEntry::getPriority))
                        .orElse(null);
                
            case "sequential":
                if (!validMotds.isEmpty()) {
                    MotdConfig.MotdEntry selected = validMotds.get(rotationIndex % validMotds.size());
                    rotationIndex++;
                    return selected;
                }
                break;
                
            case "random":
                return validMotds.get(random.nextInt(validMotds.size()));
                
            case "timed":
                // Timed rotation is handled by rotation task
                return validMotds.stream()
                        .max(Comparator.comparingInt(MotdConfig.MotdEntry::getPriority))
                        .orElse(null);
        }
        
        return validMotds.get(0);
    }
    
    /**
     * Check if all conditions for a MOTD are met
     */
    private boolean checkConditions(MotdConfig.MotdEntry motd, PaperServerListPingEvent event) {
        if (motd.getConditions().isEmpty()) return true;
        
        for (String condition : motd.getConditions()) {
            if (!checkSingleCondition(condition, event)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check a single condition
     */
    private boolean checkSingleCondition(String condition, PaperServerListPingEvent event) {
        condition = condition.trim();
        
        if (condition.startsWith("player-count:")) {
            String expr = condition.substring(13).trim();
            return checkPlayerCountCondition(expr);
        }
        
        if (condition.startsWith("time:")) {
            String timeRange = condition.substring(5).trim();
            return checkTimeCondition(timeRange);
        }
        
        if (condition.startsWith("day:")) {
            String days = condition.substring(4).trim();
            return checkDayCondition(days);
        }
        
        if (condition.startsWith("date:")) {
            String dateRange = condition.substring(5).trim();
            return checkDateCondition(dateRange);
        }
        
        if (condition.startsWith("permission:")) {
            // Can't check permission in server list ping event
            return true;
        }
        
        if (condition.startsWith("placeholder:")) {
            String expr = condition.substring(12).trim();
            return checkPlaceholderCondition(expr);
        }
        
        if (condition.startsWith("world:")) {
            // Can't check world in server list ping event
            return true;
        }
        
        return true;
    }
    
    /**
     * Check player count condition (e.g., ">50", "<10", ">=40")
     */
    private boolean checkPlayerCountCondition(String expr) {
        int currentPlayers = Bukkit.getOnlinePlayers().size();
        return ConditionUtil.evaluateNumericCondition(expr, currentPlayers);
    }
    
    /**
     * Check time condition (e.g., "18:00-23:59")
     */
    private boolean checkTimeCondition(String timeRange) {
        try {
            String[] parts = timeRange.split("-");
            if (parts.length != 2) return false;
            
            LocalTime start = LocalTime.parse(parts[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(parts[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime now = LocalTime.now();
            
            if (start.isBefore(end)) {
                return !now.isBefore(start) && !now.isAfter(end);
            } else {
                // Handle overnight range (e.g., 22:00-02:00)
                return !now.isBefore(start) || !now.isAfter(end);
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check day condition (e.g., "monday,friday,saturday")
     */
    private boolean checkDayCondition(String days) {
        String[] dayList = days.toLowerCase().split(",");
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        String currentDayName = currentDay.name().toLowerCase();
        
        for (String day : dayList) {
            if (currentDayName.equals(day.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check date condition (e.g., "12/20-12/31")
     */
    private boolean checkDateCondition(String dateRange) {
        try {
            String[] parts = dateRange.split("-");
            if (parts.length != 2) return false;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
            LocalDate start = LocalDate.parse(LocalDate.now().getYear() + "/" + parts[0].trim(), 
                    DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            LocalDate end = LocalDate.parse(LocalDate.now().getYear() + "/" + parts[1].trim(),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            LocalDate now = LocalDate.now();
            
            if (start.isBefore(end) || start.isEqual(end)) {
                return !now.isBefore(start) && !now.isAfter(end);
            } else {
                // Handle year wrap (e.g., 12/20-01/05)
                return !now.isBefore(start) || !now.isAfter(end);
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check placeholder condition
     */
    private boolean checkPlaceholderCondition(String expr) {
        // Since we can't get player in server list ping, we'll skip this for now
        return true;
    }
    
    /**
     * Process PlaceholderAPI placeholders in text
     */
    private String processPlaceholders(String text) {
        if (text == null) return "";
        
        // Replace server placeholders
        text = text.replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%server_max_players%", String.valueOf(Bukkit.getMaxPlayers()));
        
        // Use PlaceholderAPI if available
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, text);
        }
        
        return text;
    }
    
    /**
     * Downsample RGB colors to legacy colors for old clients
     */
    private String downsampleColors(String text) {
        if (config.getColorDownsamplingMethod().equalsIgnoreCase("closest-match")) {
            return ColorUtil.downsampleToLegacy(text);
        } else {
            return ColorUtil.downsampleToLegacySimple(text);
        }
    }
    
    /**
     * Handle player count modifications (fake count, custom format)
     */
    private void handlePlayerCount(PaperServerListPingEvent event) {
        if (config.isFakePlayerCountEnabled()) {
            int currentPlayers = event.getNumPlayers();
            int maxPlayers = event.getMaxPlayers();
            
            if (config.getFakePlayerCountFixed() > 0) {
                event.setNumPlayers(config.getFakePlayerCountFixed());
            } else if (config.getFakePlayerCountAdd() != 0) {
                event.setNumPlayers(currentPlayers + config.getFakePlayerCountAdd());
            }
        }
        
        if (config.getMaxPlayers() > 0) {
            event.setMaxPlayers(config.getMaxPlayers());
        }
    }
}
