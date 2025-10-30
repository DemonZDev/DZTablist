package online.demonzdevelopment.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import online.demonzdevelopment.DZTablist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages automatic update checking and plugin updates from GitHub
 * Supports version checking, downloading, and safe installation with rollback
 */
public class UpdateChecker {
    
    private final DZTablist plugin;
    private final String githubRepo = "DemonZDev/DZTablist";
    private final String githubApiUrl = "https://api.github.com/repos/" + githubRepo + "/releases";
    
    private BukkitTask updateCheckTask;
    private String latestVersion;
    private String currentVersion;
    private boolean updateAvailable = false;
    private String downloadUrl;
    private final List<GitHubRelease> availableVersions = new ArrayList<>();
    
    public UpdateChecker(DZTablist plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }
    
    /**
     * Start the update checker
     */
    public void start() {
        if (!plugin.getConfigManager().getMainConfig().isUpdateCheckerEnabled()) {
            return;
        }
        
        // Check on startup
        if (plugin.getConfigManager().getMainConfig().isUpdateCheckOnStartup()) {
            checkForUpdates(true);
        }
        
        // Schedule periodic checks
        int interval = plugin.getConfigManager().getMainConfig().getUpdateCheckInterval();
        updateCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            checkForUpdates(false);
        }, interval * 20L, interval * 20L);
    }
    
    /**
     * Stop the update checker
     */
    public void stop() {
        if (updateCheckTask != null) {
            updateCheckTask.cancel();
        }
    }
    
    /**
     * Check for available updates
     */
    public CompletableFuture<Boolean> checkForUpdates(boolean startup) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                availableVersions.clear();
                
                URL url = new URL(githubApiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                
                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JsonArray releases = JsonParser.parseString(response.toString()).getAsJsonArray();
                    
                    if (releases.size() > 0) {
                        for (int i = 0; i < releases.size(); i++) {
                            JsonObject release = releases.get(i).getAsJsonObject();
                            
                            String version = release.get("tag_name").getAsString();
                            String releaseName = release.get("name").getAsString();
                            boolean prerelease = release.get("prerelease").getAsBoolean();
                            String publishedAt = release.get("published_at").getAsString();
                            String body = release.has("body") ? release.get("body").getAsString() : "";
                            
                            JsonArray assets = release.getAsJsonArray("assets");
                            String assetDownloadUrl = null;
                            
                            if (assets.size() > 0) {
                                for (int j = 0; j < assets.size(); j++) {
                                    JsonObject asset = assets.get(j).getAsJsonObject();
                                    String assetName = asset.get("name").getAsString();
                                    
                                    if (assetName.endsWith(".jar")) {
                                        assetDownloadUrl = asset.get("browser_download_url").getAsString();
                                        break;
                                    }
                                }
                            }
                            
                            availableVersions.add(new GitHubRelease(
                                    version,
                                    releaseName,
                                    prerelease,
                                    publishedAt,
                                    body,
                                    assetDownloadUrl
                            ));
                        }
                        
                        // Get latest stable version
                        GitHubRelease latest = availableVersions.stream()
                                .filter(r -> !r.isPrerelease())
                                .findFirst()
                                .orElse(availableVersions.get(0));
                        
                        latestVersion = latest.getVersion();
                        downloadUrl = latest.getDownloadUrl();
                        
                        if (compareVersions(latestVersion, currentVersion) > 0) {
                            updateAvailable = true;
                            notifyUpdate(startup);
                            return true;
                        } else {
                            updateAvailable = false;
                        }
                    }
                }
                
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
            
            return false;
        });
    }
    
    /**
     * Notify about available updates
     */
    private void notifyUpdate(boolean startup) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getConfigManager().getMainConfig().isUpdateNotifyConsole()) {
                plugin.getLogger().info("╔════════════════════════════════════════╗");
                plugin.getLogger().info("║     DZTablist Update Available!       ║");
                plugin.getLogger().info("╠════════════════════════════════════════╣");
                plugin.getLogger().info("║  Current: " + currentVersion);
                plugin.getLogger().info("║  Latest:  " + latestVersion);
                plugin.getLogger().info("║                                        ║");
                plugin.getLogger().info("║  Use /dztablist update latest          ║");
                plugin.getLogger().info("╚════════════════════════════════════════╝");
            }
            
            if (plugin.getConfigManager().getMainConfig().isUpdateNotifyAdmins()) {
                String message = "§8[§bDZTablist§8] §aUpdate available! §7(" + currentVersion + " → " + latestVersion + ")";
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("dztablist.admin")) {
                        player.sendMessage(message);
                        player.sendMessage("§8[§bDZTablist§8] §7Use §f/dztablist update latest §7to update");
                    }
                }
            }
        });
    }
    
    /**
     * Notify players joining about updates
     */
    public void notifyPlayerJoin(Player player) {
        if (!updateAvailable) return;
        if (!plugin.getConfigManager().getMainConfig().isUpdateNotifyOnJoin()) return;
        if (!player.hasPermission("dztablist.admin")) return;
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("§8[§bDZTablist§8] §aUpdate available! §7(" + currentVersion + " → " + latestVersion + ")");
            player.sendMessage("§8[§bDZTablist§8] §7Use §f/dztablist update latest §7to update");
        }, 40L);
    }
    
    /**
     * Download and install a specific version
     */
    public CompletableFuture<Boolean> downloadAndInstall(String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GitHubRelease release = findRelease(version);
                if (release == null || release.getDownloadUrl() == null) {
                    plugin.getLogger().warning("Version " + version + " not found or no download available");
                    return false;
                }
                
                plugin.getLogger().info("Downloading DZTablist " + version + "...");
                
                // Backup current plugin
                File currentJar = getPluginJar();
                if (currentJar != null) {
                    File backupDir = new File(plugin.getDataFolder(), "backups");
                    backupDir.mkdirs();
                    
                    File backup = new File(backupDir, "DZTablist-" + currentVersion + "-backup.jar");
                    Files.copy(currentJar.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    plugin.getLogger().info("Backed up current version to: " + backup.getName());
                }
                
                // Download new version
                URL url = new URL(release.getDownloadUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                
                if (connection.getResponseCode() == 200) {
                    File updateFolder = new File("plugins/update");
                    updateFolder.mkdirs();
                    
                    File updateFile = new File(updateFolder, "DZTablist.jar");
                    
                    try (InputStream in = connection.getInputStream();
                         FileOutputStream out = new FileOutputStream(updateFile)) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalBytes = 0;
                        
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                        }
                        
                        plugin.getLogger().info("Downloaded " + (totalBytes / 1024) + " KB successfully!");
                        plugin.getLogger().info("Update will be installed on next server restart/reload");
                        
                        return true;
                    }
                }
                
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to download update: " + e.getMessage());
                e.printStackTrace();
            }
            
            return false;
        });
    }
    
    /**
     * Find a release by version
     */
    private GitHubRelease findRelease(String version) {
        return availableVersions.stream()
                .filter(r -> r.getVersion().equalsIgnoreCase(version) || 
                           r.getVersion().equalsIgnoreCase("v" + version))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get the current plugin JAR file
     */
    private File getPluginJar() {
        try {
            return new File(plugin.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Compare two version strings
     * Returns: 1 if v1 > v2, -1 if v1 < v2, 0 if equal
     */
    private int compareVersions(String v1, String v2) {
        // Remove 'v' prefix if present
        v1 = v1.startsWith("v") ? v1.substring(1) : v1;
        v2 = v2.startsWith("v") ? v2.substring(1) : v2;
        
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;
            
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        
        return 0;
    }
    
    /**
     * Parse version part (handles suffixes like "1.0.0-SNAPSHOT")
     */
    private int parseVersionPart(String part) {
        try {
            // Remove any non-numeric suffix
            part = part.split("-")[0];
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Get version relative to current (next, previous)
     */
    public GitHubRelease getRelativeVersion(String relative) {
        List<GitHubRelease> sortedVersions = availableVersions.stream()
                .filter(r -> !r.isPrerelease())
                .sorted((r1, r2) -> compareVersions(r2.getVersion(), r1.getVersion()))
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortedVersions.size(); i++) {
            if (compareVersions(sortedVersions.get(i).getVersion(), currentVersion) == 0) {
                if (relative.equalsIgnoreCase("next") && i > 0) {
                    return sortedVersions.get(i - 1);
                } else if (relative.equalsIgnoreCase("previous") && i < sortedVersions.size() - 1) {
                    return sortedVersions.get(i + 1);
                }
            }
        }
        
        return null;
    }
    
    // Getters
    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }
    public String getCurrentVersion() { return currentVersion; }
    public List<GitHubRelease> getAvailableVersions() { return new ArrayList<>(availableVersions); }
    
    /**
     * GitHub Release data class
     */
    public static class GitHubRelease {
        private final String version;
        private final String name;
        private final boolean prerelease;
        private final String publishedAt;
        private final String description;
        private final String downloadUrl;
        
        public GitHubRelease(String version, String name, boolean prerelease, 
                           String publishedAt, String description, String downloadUrl) {
            this.version = version;
            this.name = name;
            this.prerelease = prerelease;
            this.publishedAt = publishedAt;
            this.description = description;
            this.downloadUrl = downloadUrl;
        }
        
        public String getVersion() { return version; }
        public String getName() { return name; }
        public boolean isPrerelease() { return prerelease; }
        public String getPublishedAt() { return publishedAt; }
        public String getDescription() { return description; }
        public String getDownloadUrl() { return downloadUrl; }
    }
}
