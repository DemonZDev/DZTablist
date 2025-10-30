package online.demonzdevelopment;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import online.demonzdevelopment.api.DZTablistAPI;
import online.demonzdevelopment.command.BossbarCommand;
import online.demonzdevelopment.command.DZTablistCommand;
import online.demonzdevelopment.command.ScoreboardCommand;
import online.demonzdevelopment.config.*;
import online.demonzdevelopment.database.DatabaseManager;
import online.demonzdevelopment.listener.*;
import online.demonzdevelopment.managers.*;
import online.demonzdevelopment.util.ColorUtil;
import online.demonzdevelopment.util.ErrorLogger;
import online.demonzdevelopment.util.PerformanceMonitor;
import online.demonzdevelopment.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * DZTablist - Advanced Tablist Plugin
 * Main plugin class handling initialization and management
 * 
 * @author DemonZ Development
 * @version 1.0.0
 */
public class DZTablist extends JavaPlugin {
    
    private static DZTablist instance;
    private static DZTablistAPI api;
    
    // Managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private PlaceholderManager placeholderManager;
    private HeaderFooterManager headerFooterManager;
    private NameTagManager nameTagManager;
    private SortingManager sortingManager;
    private TablistFormatManager tablistFormatManager;
    private PlayerlistObjectiveManager playerlistObjectiveManager;
    private BelownameManager belownameManager;
    private BossbarManager bossbarManager;
    private ScoreboardManager scoreboardManager;
    private LayoutManager layoutManager;
    private PerWorldPlayerlistManager perWorldPlayerlistManager;
    private MotdManager motdManager;
    
    // Utilities
    private ErrorLogger errorLogger;
    private PerformanceMonitor performanceMonitor;
    private MiniMessage miniMessage;
    private UpdateChecker updateChecker;
    
    // External dependencies
    private LuckPerms luckPerms;
    
    @Override
    public void onEnable() {
        instance = this;
        
        long startTime = System.currentTimeMillis();
        
        getLogger().info("════════════════════════════════════");
        getLogger().info("  DZTablist v" + getDescription().getVersion());
        getLogger().info("  By DemonZ Development");
        getLogger().info("════════════════════════════════════");
        
        // Initialize utilities
        errorLogger = new ErrorLogger(this);
        performanceMonitor = new PerformanceMonitor();
        miniMessage = MiniMessage.miniMessage();
        
        // Check dependencies
        if (!checkDependencies()) {
            getLogger().severe("Missing required dependencies! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Load configurations
        configManager = new ConfigManager(this);
        configManager.loadAllConfigs();
        
        // Initialize database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        
        // Initialize managers
        initializeManagers();
        
        // Register listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        // Initialize API
        api = new DZTablistAPI(this);
        
        // Initialize MOTD system
        if (configManager.getMotdConfig().isEnabled()) {
            motdManager = new MotdManager(this);
            getLogger().info("✓ MOTD system initialized");
        }
        
        // Initialize Update Checker
        updateChecker = new UpdateChecker(this);
        updateChecker.start();
        
        // Schedule tasks
        scheduleTasks();
        
        long loadTime = System.currentTimeMillis() - startTime;
        getLogger().info("════════════════════════════════════");
        getLogger().info("  Successfully enabled in " + loadTime + "ms");
        getLogger().info("════════════════════════════════════");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Disabling DZTablist...");
        
        // Disable managers
        if (headerFooterManager != null) headerFooterManager.shutdown();
        if (nameTagManager != null) nameTagManager.shutdown();
        if (sortingManager != null) sortingManager.shutdown();
        if (tablistFormatManager != null) tablistFormatManager.shutdown();
        if (playerlistObjectiveManager != null) playerlistObjectiveManager.shutdown();
        if (belownameManager != null) belownameManager.shutdown();
        if (bossbarManager != null) bossbarManager.shutdown();
        if (scoreboardManager != null) scoreboardManager.shutdown();
        if (layoutManager != null) layoutManager.shutdown();
        if (perWorldPlayerlistManager != null) perWorldPlayerlistManager.shutdown();
        if (placeholderManager != null) placeholderManager.shutdown();
        if (motdManager != null) motdManager.shutdown();
        
        // Stop update checker
        if (updateChecker != null) updateChecker.stop();
        
        // Close database
        if (databaseManager != null) databaseManager.close();
        
        getLogger().info("DZTablist disabled successfully!");
    }
    
    /**
     * Check if required dependencies are present
     */
    private boolean checkDependencies() {
        // Check LuckPerms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            getLogger().severe("LuckPerms not found! This plugin requires LuckPerms to function.");
            return false;
        }
        luckPerms = provider.getProvider();
        getLogger().info("✓ LuckPerms hooked successfully");
        
        // Check PlaceholderAPI
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("PlaceholderAPI not found! This plugin requires PlaceholderAPI to function.");
            return false;
        }
        getLogger().info("✓ PlaceholderAPI hooked successfully");
        
        return true;
    }
    
    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        placeholderManager = new PlaceholderManager(this);
        placeholderManager.initialize();
        
        if (configManager.getMainConfig().isFeatureEnabled("header-footer")) {
            headerFooterManager = new HeaderFooterManager(this);
            headerFooterManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("nametags")) {
            nameTagManager = new NameTagManager(this);
            nameTagManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("tablist-format")) {
            sortingManager = new SortingManager(this);
            sortingManager.initialize();
            
            tablistFormatManager = new TablistFormatManager(this);
            tablistFormatManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("playerlist-objective")) {
            playerlistObjectiveManager = new PlayerlistObjectiveManager(this);
            playerlistObjectiveManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("belowname")) {
            belownameManager = new BelownameManager(this);
            belownameManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("bossbar")) {
            bossbarManager = new BossbarManager(this);
            bossbarManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("scoreboard")) {
            scoreboardManager = new ScoreboardManager(this);
            scoreboardManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("layout")) {
            layoutManager = new LayoutManager(this);
            layoutManager.initialize();
        }
        
        if (configManager.getMainConfig().isFeatureEnabled("per-world-playerlist")) {
            perWorldPlayerlistManager = new PerWorldPlayerlistManager(this);
            perWorldPlayerlistManager.initialize();
        }
    }
    
    /**
     * Register event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerWorldChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new LuckPermsListener(this), this);
    }
    
    /**
     * Register commands
     */
    private void registerCommands() {
        getCommand("dztablist").setExecutor(new DZTablistCommand(this));
        getCommand("bossbar").setExecutor(new BossbarCommand(this));
        getCommand("scoreboard").setExecutor(new ScoreboardCommand(this));
    }
    
    /**
     * Schedule repeating tasks
     */
    private void scheduleTasks() {
        // Tasks are scheduled by individual managers
        getLogger().info("All tasks scheduled successfully");
    }
    
    /**
     * Reload all configurations and managers
     */
    public void reload() {
        getLogger().info("Reloading DZTablist...");
        
        // Reload configurations
        configManager.loadAllConfigs();
        
        // Reload managers
        if (headerFooterManager != null) headerFooterManager.reload();
        if (nameTagManager != null) nameTagManager.reload();
        if (sortingManager != null) sortingManager.reload();
        if (tablistFormatManager != null) tablistFormatManager.reload();
        if (playerlistObjectiveManager != null) playerlistObjectiveManager.reload();
        if (belownameManager != null) belownameManager.reload();
        if (bossbarManager != null) bossbarManager.reload();
        if (scoreboardManager != null) scoreboardManager.reload();
        if (layoutManager != null) layoutManager.reload();
        if (perWorldPlayerlistManager != null) perWorldPlayerlistManager.reload();
        if (placeholderManager != null) placeholderManager.reload();
        if (motdManager != null) motdManager.reload();
        
        getLogger().info("Reload complete!");
    }
    
    // Getters
    public static DZTablist getInstance() { return instance; }
    public static DZTablistAPI getApi() { return api; }
    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlaceholderManager getPlaceholderManager() { return placeholderManager; }
    public HeaderFooterManager getHeaderFooterManager() { return headerFooterManager; }
    public NameTagManager getNameTagManager() { return nameTagManager; }
    public SortingManager getSortingManager() { return sortingManager; }
    public TablistFormatManager getTablistFormatManager() { return tablistFormatManager; }
    public PlayerlistObjectiveManager getPlayerlistObjectiveManager() { return playerlistObjectiveManager; }
    public BelownameManager getBelownameManager() { return belownameManager; }
    public BossbarManager getBossbarManager() { return bossbarManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public LayoutManager getLayoutManager() { return layoutManager; }
    public PerWorldPlayerlistManager getPerWorldPlayerlistManager() { return perWorldPlayerlistManager; }
    public ErrorLogger getErrorLogger() { return errorLogger; }
    public PerformanceMonitor getPerformanceMonitor() { return performanceMonitor; }
    public MiniMessage getMiniMessage() { return miniMessage; }
    public LuckPerms getLuckPerms() { return luckPerms; }
    public MotdManager getMotdManager() { return motdManager; }
    public UpdateChecker getUpdateChecker() { return updateChecker; }
}
