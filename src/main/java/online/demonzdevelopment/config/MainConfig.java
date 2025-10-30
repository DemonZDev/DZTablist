package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Main configuration handler
 */
public class MainConfig {
    
    private final FileConfiguration config;
    
    public MainConfig(FileConfiguration config) {
        this.config = config;
    }
    
    // Database settings
    public String getDatabaseType() {
        return config.getString("database.type", "YAML");
    }
    
    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }
    
    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "dztablist");
    }
    
    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }
    
    public int getMySQLPoolSize() {
        return config.getInt("database.mysql.pool-size", 10);
    }
    
    public String getSQLiteFile() {
        return config.getString("database.sqlite.file", "data.db");
    }
    
    // Performance settings
    public boolean isAsyncProcessing() {
        return config.getBoolean("performance.async-processing", true);
    }
    
    public int getRefreshInterval(String feature) {
        return config.getInt("performance.refresh-intervals." + feature, 1000);
    }
    
    public int getCacheTTL() {
        return config.getInt("performance.cache-ttl", 5000);
    }
    
    public boolean isFeatureEnabled(String feature) {
        return config.getBoolean("performance.feature-toggles." + feature, true);
    }
    
    // Logging settings
    public String getLogFile() {
        return config.getString("logging.file", "errors.log");
    }
    
    public boolean isConsoleSpamReduction() {
        return config.getBoolean("logging.console-spam-reduction", true);
    }
    
    public boolean isDebugMode() {
        return config.getBoolean("logging.debug-mode", false);
    }
    
    // Placeholder settings
    public boolean isPAPISupport() {
        return config.getBoolean("placeholders.papi-support", true);
    }
    
    public boolean isRelationalSupport() {
        return config.getBoolean("placeholders.relational-support", true);
    }
    
    public int getPlaceholderRefreshInterval() {
        return config.getInt("placeholders.refresh-interval", 1000);
    }
    
    // Anti-override settings
    public boolean isAntiOverrideEnabled() {
        return config.getBoolean("anti-override.enabled", true);
    }
    
    public String getAntiOverridePriority() {
        return config.getString("anti-override.priority", "HIGHEST");
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // Update checker settings
    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("update-checker.enabled", true);
    }
    
    public boolean isUpdateCheckOnStartup() {
        return config.getBoolean("update-checker.check-on-startup", true);
    }
    
    public int getUpdateCheckInterval() {
        return config.getInt("update-checker.runtime-check-interval", 3600);
    }
    
    public boolean isUpdateNotifyConsole() {
        return config.getBoolean("update-checker.notify-console", true);
    }
    
    public boolean isUpdateNotifyAdmins() {
        return config.getBoolean("update-checker.notify-admins", true);
    }
    
    public boolean isUpdateNotifyOnJoin() {
        return config.getBoolean("update-checker.notify-on-join", true);
    }
}
