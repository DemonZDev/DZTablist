package online.demonzdevelopment.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import online.demonzdevelopment.DZTablist;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles database operations for player data
 */
public class DatabaseManager {
    
    private final DZTablist plugin;
    private HikariDataSource dataSource;
    private DatabaseType databaseType;
    
    // Cache for player toggles
    private final Map<UUID, Boolean> bossbarToggles = new HashMap<>();
    private final Map<UUID, Boolean> scoreboardToggles = new HashMap<>();
    
    public DatabaseManager(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize database connection
     */
    public void initialize() {
        String type = plugin.getConfigManager().getMainConfig().getDatabaseType();
        databaseType = DatabaseType.valueOf(type.toUpperCase());
        
        switch (databaseType) {
            case MYSQL:
                initializeMySQL();
                break;
            case SQLITE:
                initializeSQLite();
                break;
            case YAML:
                // YAML doesn't need database connection
                plugin.getLogger().info("Using YAML storage");
                return;
        }
        
        createTables();
        loadToggles();
        
        plugin.getLogger().info("Database initialized successfully (" + databaseType + ")");
    }
    
    /**
     * Initialize MySQL connection
     */
    private void initializeMySQL() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + plugin.getConfigManager().getMainConfig().getMySQLHost() + 
                ":" + plugin.getConfigManager().getMainConfig().getMySQLPort() + 
                "/" + plugin.getConfigManager().getMainConfig().getMySQLDatabase());
        config.setUsername(plugin.getConfigManager().getMainConfig().getMySQLUsername());
        config.setPassword(plugin.getConfigManager().getMainConfig().getMySQLPassword());
        config.setMaximumPoolSize(plugin.getConfigManager().getMainConfig().getMySQLPoolSize());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        dataSource = new HikariDataSource(config);
    }
    
    /**
     * Initialize SQLite connection
     */
    private void initializeSQLite() {
        HikariConfig config = new HikariConfig();
        File dbFile = new File(plugin.getDataFolder(), plugin.getConfigManager().getMainConfig().getSQLiteFile());
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setMaximumPoolSize(1);
        
        dataSource = new HikariDataSource(config);
    }
    
    /**
     * Create database tables
     */
    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "bossbar_toggle BOOLEAN DEFAULT TRUE," +
                "scoreboard_toggle BOOLEAN DEFAULT TRUE" +
                ")";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getErrorLogger().log("Failed to create tables", e);
        }
    }
    
    /**
     * Load toggles from database
     */
    private void loadToggles() {
        String sql = "SELECT * FROM player_data";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                bossbarToggles.put(uuid, rs.getBoolean("bossbar_toggle"));
                scoreboardToggles.put(uuid, rs.getBoolean("scoreboard_toggle"));
            }
        } catch (SQLException e) {
            plugin.getErrorLogger().log("Failed to load toggles", e);
        }
    }
    
    /**
     * Save player toggle state
     */
    public void saveToggle(UUID uuid, String type, boolean enabled) {
        if (databaseType == DatabaseType.YAML) {
            // Save to YAML config
            if (type.equals("bossbar")) {
                bossbarToggles.put(uuid, enabled);
            } else if (type.equals("scoreboard")) {
                scoreboardToggles.put(uuid, enabled);
            }
            return;
        }
        
        String sql = "INSERT INTO player_data (uuid, " + type + "_toggle) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " + type + "_toggle = ?";
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.setBoolean(2, enabled);
                stmt.setBoolean(3, enabled);
                stmt.executeUpdate();
                
                if (type.equals("bossbar")) {
                    bossbarToggles.put(uuid, enabled);
                } else if (type.equals("scoreboard")) {
                    scoreboardToggles.put(uuid, enabled);
                }
            } catch (SQLException e) {
                plugin.getErrorLogger().log("Failed to save toggle", e);
            }
        });
    }
    
    /**
     * Get bossbar toggle state
     */
    public boolean isBossbarEnabled(UUID uuid) {
        return bossbarToggles.getOrDefault(uuid, true);
    }
    
    /**
     * Get scoreboard toggle state
     */
    public boolean isScoreboardEnabled(UUID uuid) {
        return scoreboardToggles.getOrDefault(uuid, true);
    }
    
    /**
     * Close database connection
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    public enum DatabaseType {
        YAML, MYSQL, SQLITE
    }
}
