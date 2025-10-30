package online.demonzdevelopment.config;

import online.demonzdevelopment.DZTablist;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Manages all plugin configurations
 */
public class ConfigManager {
    
    private final DZTablist plugin;
    
    private MainConfig mainConfig;
    private HeaderFooterConfig headerFooterConfig;
    private NameTagConfig nameTagConfig;
    private SortingConfig sortingConfig;
    private TablistFormatConfig tablistFormatConfig;
    private PlayerlistObjectiveConfig playerlistObjectiveConfig;
    private BelownameConfig belownameConfig;
    private BossbarConfig bossbarConfig;
    private ScoreboardConfig scoreboardConfig;
    private PerWorldPlayerlistConfig perWorldPlayerlistConfig;
    private LayoutConfig layoutConfig;
    private PlaceholderConfig placeholderConfig;
    private ConditionalPlaceholderConfig conditionalPlaceholderConfig;
    private PlaceholderReplacementConfig placeholderReplacementConfig;
    private MotdConfig motdConfig;
    
    public ConfigManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load all configuration files
     */
    public void loadAllConfigs() {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Save default configs if they don't exist
        saveDefaultConfig("config.yml");
        saveDefaultConfig("header-footer.yml");
        saveDefaultConfig("nametags.yml");
        saveDefaultConfig("sorting.yml");
        saveDefaultConfig("tablist-format.yml");
        saveDefaultConfig("playerlist-objective.yml");
        saveDefaultConfig("belowname.yml");
        saveDefaultConfig("bossbar.yml");
        saveDefaultConfig("scoreboard.yml");
        saveDefaultConfig("per-world-playerlist.yml");
        saveDefaultConfig("layout.yml");
        saveDefaultConfig("placeholders.yml");
        saveDefaultConfig("conditional-placeholders.yml");
        saveDefaultConfig("placeholder-replacements.yml");
        saveDefaultConfig("motd.yml");
        
        // Load configurations
        mainConfig = new MainConfig(loadConfig("config.yml"));
        headerFooterConfig = new HeaderFooterConfig(loadConfig("header-footer.yml"));
        nameTagConfig = new NameTagConfig(loadConfig("nametags.yml"));
        sortingConfig = new SortingConfig(loadConfig("sorting.yml"));
        tablistFormatConfig = new TablistFormatConfig(loadConfig("tablist-format.yml"));
        playerlistObjectiveConfig = new PlayerlistObjectiveConfig(loadConfig("playerlist-objective.yml"));
        belownameConfig = new BelownameConfig(loadConfig("belowname.yml"));
        bossbarConfig = new BossbarConfig(loadConfig("bossbar.yml"));
        scoreboardConfig = new ScoreboardConfig(loadConfig("scoreboard.yml"));
        perWorldPlayerlistConfig = new PerWorldPlayerlistConfig(loadConfig("per-world-playerlist.yml"));
        layoutConfig = new LayoutConfig(loadConfig("layout.yml"));
        placeholderConfig = new PlaceholderConfig(loadConfig("placeholders.yml"));
        conditionalPlaceholderConfig = new ConditionalPlaceholderConfig(loadConfig("conditional-placeholders.yml"));
        placeholderReplacementConfig = new PlaceholderReplacementConfig(loadConfig("placeholder-replacements.yml"));
        motdConfig = new MotdConfig(plugin);
        
        plugin.getLogger().info("All configurations loaded successfully!");
    }
    
    /**
     * Save default configuration file if it doesn't exist
     */
    private void saveDefaultConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try (InputStream in = plugin.getResource(fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save default config: " + fileName);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Load a configuration file
     */
    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }
    
    // Getters
    public MainConfig getMainConfig() { return mainConfig; }
    public HeaderFooterConfig getHeaderFooterConfig() { return headerFooterConfig; }
    public NameTagConfig getNameTagConfig() { return nameTagConfig; }
    public SortingConfig getSortingConfig() { return sortingConfig; }
    public TablistFormatConfig getTablistFormatConfig() { return tablistFormatConfig; }
    public PlayerlistObjectiveConfig getPlayerlistObjectiveConfig() { return playerlistObjectiveConfig; }
    public BelownameConfig getBelownameConfig() { return belownameConfig; }
    public BossbarConfig getBossbarConfig() { return bossbarConfig; }
    public ScoreboardConfig getScoreboardConfig() { return scoreboardConfig; }
    public PerWorldPlayerlistConfig getPerWorldPlayerlistConfig() { return perWorldPlayerlistConfig; }
    public LayoutConfig getLayoutConfig() { return layoutConfig; }
    public PlaceholderConfig getPlaceholderConfig() { return placeholderConfig; }
    public ConditionalPlaceholderConfig getConditionalPlaceholderConfig() { return conditionalPlaceholderConfig; }
    public PlaceholderReplacementConfig getPlaceholderReplacementConfig() { return placeholderReplacementConfig; }
    public MotdConfig getMotdConfig() { return motdConfig; }
}
