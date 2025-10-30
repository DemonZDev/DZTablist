package online.demonzdevelopment.utils;

import online.demonzdevelopment.DZTablist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles error logging to file
 */
public class ErrorLogger {
    
    private final DZTablist plugin;
    private final File logFile;
    private final DateTimeFormatter formatter;
    
    public ErrorLogger(DZTablist plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), plugin.getConfigManager() != null ? 
            plugin.getConfigManager().getMainConfig().getLogFile() : "errors.log");
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }
    
    /**
     * Log an error to file
     */
    public void log(String message, Throwable throwable) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
            writer.println("════════════════════════════════════");
            writer.println("[" + LocalDateTime.now().format(formatter) + "]");
            writer.println("Message: " + message);
            if (throwable != null) {
                writer.println("Exception: " + throwable.getClass().getName());
                writer.println("Message: " + throwable.getMessage());
                writer.println("Stack Trace:");
                throwable.printStackTrace(writer);
            }
            writer.println("════════════════════════════════════");
            writer.println();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to error log: " + e.getMessage());
        }
        
        // Log to console if not spam reduction
        if (plugin.getConfigManager() == null || !plugin.getConfigManager().getMainConfig().isConsoleSpamReduction()) {
            plugin.getLogger().severe(message);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }
    
    /**
     * Log an error to file
     */
    public void log(String message) {
        log(message, null);
    }
    
    /**
     * Log a warning to file
     */
    public void warn(String message) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
            writer.println("[" + LocalDateTime.now().format(formatter) + "] [WARNING] " + message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to error log: " + e.getMessage());
        }
        
        if (plugin.getConfigManager() == null || !plugin.getConfigManager().getMainConfig().isConsoleSpamReduction()) {
            plugin.getLogger().warning(message);
        }
    }
}
