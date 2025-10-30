package online.demonzdevelopment.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.util.PerformanceMonitor;
import online.demonzdevelopment.util.UpdateChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main command handler with update management and credit system
 */
public class DZTablistCommand implements CommandExecutor, TabCompleter {
    
    private final DZTablist plugin;
    
    public DZTablistCommand(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("dztablist.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "cpu":
            case "performance":
                handleCPU(sender);
                break;
            case "update":
                handleUpdate(sender, args);
                break;
            case "credit":
            case "credits":
                handleCredit(sender);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sender.sendMessage(Component.text("Unknown subcommand. Use /dztablist help").color(NamedTextColor.RED));
        }
        
        return true;
    }
    
    /**
     * Handle reload command
     */
    private void handleReload(CommandSender sender) {
        sender.sendMessage(Component.text("⟳ Reloading DZTablist...").color(NamedTextColor.YELLOW));
        
        try {
            plugin.reload();
            sender.sendMessage(Component.text("✓ DZTablist reloaded successfully!").color(NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("✗ Failed to reload: " + e.getMessage()).color(NamedTextColor.RED));
            plugin.getErrorLogger().log("Failed to reload plugin", e);
        }
    }
    
    /**
     * Handle CPU/Performance command
     */
    private void handleCPU(CommandSender sender) {
        sender.sendMessage(Component.text("╔════════════════════════════════════╗").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("DZTablist Performance Monitor").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                .append(Component.text("  ║").color(NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("╠════════════════════════════════════╣").color(NamedTextColor.DARK_GRAY));
        
        Map<String, PerformanceMonitor.FeatureStats> stats = plugin.getPerformanceMonitor().getAllStats();
        
        if (stats.isEmpty()) {
            sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("No data available yet").color(NamedTextColor.YELLOW))
                    .append(Component.text("           ║").color(NamedTextColor.DARK_GRAY)));
        } else {
            double totalAvg = 0;
            for (Map.Entry<String, PerformanceMonitor.FeatureStats> entry : stats.entrySet()) {
                PerformanceMonitor.FeatureStats stat = entry.getValue();
                String featureName = entry.getKey();
                if (featureName.length() > 18) featureName = featureName.substring(0, 15) + "...";
                
                NamedTextColor color = stat.getAverageTime() < 1.0 ? NamedTextColor.GREEN :
                                      stat.getAverageTime() < 5.0 ? NamedTextColor.YELLOW :
                                      NamedTextColor.RED;
                
                sender.sendMessage(Component.text("║ ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(String.format("%-18s %5.2fms", featureName, stat.getAverageTime())).color(color))
                        .append(Component.text(" ║").color(NamedTextColor.DARK_GRAY)));
                totalAvg += stat.getAverageTime();
            }
            
            sender.sendMessage(Component.text("╠════════════════════════════════════╣").color(NamedTextColor.DARK_GRAY));
            sender.sendMessage(Component.text("║ ").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text(String.format("Total: %.2fms avg", totalAvg)).color(NamedTextColor.GREEN))
                    .append(Component.text("              ║").color(NamedTextColor.DARK_GRAY)));
        }
        
        sender.sendMessage(Component.text("╚════════════════════════════════════╝").color(NamedTextColor.DARK_GRAY));
    }
    
    /**
     * Handle update command
     */
    private void handleUpdate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("╔════════════════════════════════════╗").color(NamedTextColor.DARK_GRAY));
            sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("DZTablist Update Manager").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text("     ║").color(NamedTextColor.DARK_GRAY)));
            sender.sendMessage(Component.text("╠════════════════════════════════════╣").color(NamedTextColor.DARK_GRAY));
            sendHelpLine(sender, "/dztablist update check", "Check for updates");
            sendHelpLine(sender, "/dztablist update latest", "Update to latest");
            sendHelpLine(sender, "/dztablist update next", "Update to next version");
            sendHelpLine(sender, "/dztablist update previous", "Downgrade to previous");
            sendHelpLine(sender, "/dztablist update <version>", "Install specific version");
            sender.sendMessage(Component.text("╚════════════════════════════════════╝").color(NamedTextColor.DARK_GRAY));
            return;
        }
        
        String action = args[1].toLowerCase();
        UpdateChecker checker = plugin.getUpdateChecker();
        
        switch (action) {
            case "check":
                sender.sendMessage(Component.text("⟳ Checking for updates...").color(NamedTextColor.YELLOW));
                checker.checkForUpdates(false).thenAccept(available -> {
                    if (available) {
                        sender.sendMessage(Component.text("✓ Update available: " + checker.getLatestVersion()).color(NamedTextColor.GREEN));
                        sender.sendMessage(Component.text("  Current: " + checker.getCurrentVersion()).color(NamedTextColor.GRAY));
                        sender.sendMessage(Component.text("  Use: /dztablist update latest").color(NamedTextColor.AQUA));
                    } else {
                        sender.sendMessage(Component.text("✓ Latest version installed!").color(NamedTextColor.GREEN));
                    }
                });
                break;
                
            case "latest":
                sender.sendMessage(Component.text("⟳ Downloading latest version...").color(NamedTextColor.YELLOW));
                checker.checkForUpdates(false).thenCompose(available -> {
                    if (!available) {
                        sender.sendMessage(Component.text("✓ Already on latest version!").color(NamedTextColor.GREEN));
                        return null;
                    }
                    return checker.downloadAndInstall(checker.getLatestVersion());
                }).thenAccept(success -> {
                    if (success != null && success) {
                        sender.sendMessage(Component.text("✓ Downloaded successfully!").color(NamedTextColor.GREEN));
                        sender.sendMessage(Component.text("  Restart server to apply").color(NamedTextColor.AQUA));
                    } else if (success != null) {
                        sender.sendMessage(Component.text("✗ Download failed").color(NamedTextColor.RED));
                    }
                });
                break;
                
            case "next":
                UpdateChecker.GitHubRelease nextRelease = checker.getRelativeVersion("next");
                if (nextRelease != null) {
                    sender.sendMessage(Component.text("⟳ Downloading " + nextRelease.getVersion() + "...").color(NamedTextColor.YELLOW));
                    checker.downloadAndInstall(nextRelease.getVersion()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("✓ Downloaded " + nextRelease.getVersion() + "!").color(NamedTextColor.GREEN));
                            sender.sendMessage(Component.text("  Restart server to apply").color(NamedTextColor.AQUA));
                        } else {
                            sender.sendMessage(Component.text("✗ Download failed").color(NamedTextColor.RED));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("✗ No newer version available").color(NamedTextColor.RED));
                }
                break;
                
            case "previous":
                UpdateChecker.GitHubRelease prevRelease = checker.getRelativeVersion("previous");
                if (prevRelease != null) {
                    sender.sendMessage(Component.text("⟳ Downloading " + prevRelease.getVersion() + "...").color(NamedTextColor.YELLOW));
                    checker.downloadAndInstall(prevRelease.getVersion()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("✓ Downloaded " + prevRelease.getVersion() + "!").color(NamedTextColor.GREEN));
                            sender.sendMessage(Component.text("  Restart server to apply").color(NamedTextColor.AQUA));
                        } else {
                            sender.sendMessage(Component.text("✗ Download failed").color(NamedTextColor.RED));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("✗ No previous version found").color(NamedTextColor.RED));
                }
                break;
                
            default:
                // Specific version
                String version = action;
                sender.sendMessage(Component.text("⟳ Downloading " + version + "...").color(NamedTextColor.YELLOW));
                checker.downloadAndInstall(version).thenAccept(success -> {
                    if (success) {
                        sender.sendMessage(Component.text("✓ Downloaded " + version + "!").color(NamedTextColor.GREEN));
                        sender.sendMessage(Component.text("  Restart server to apply").color(NamedTextColor.AQUA));
                    } else {
                        sender.sendMessage(Component.text("✗ Version " + version + " not found").color(NamedTextColor.RED));
                    }
                });
        }
    }
    
    /**
     * Handle credit command
     */
    private void handleCredit(CommandSender sender) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("╔════════════════════════════════════╗").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("║   ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("Made by DemonZ Development").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("      ║").color(NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("╠════════════════════════════════════╣").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("DemonZ Development Ecosystem").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                .append(Component.text("      ║").color(NamedTextColor.DARK_GRAY)));
        
        // demonzdevelopment.online
        sender.sendMessage(Component.text("║  • ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("demonzdevelopment.online").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl("https://demonzdevelopment.online"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to visit!").color(NamedTextColor.GREEN))))
                .append(Component.text("        ║").color(NamedTextColor.DARK_GRAY)));
        
        // hyzerox.me
        sender.sendMessage(Component.text("║  • ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("hyzerox.me").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl("https://hyzerox.me"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to visit!").color(NamedTextColor.GREEN))))
                .append(Component.text("                      ║").color(NamedTextColor.DARK_GRAY)));
        
        sender.sendMessage(Component.text("╚════════════════════════════════════╝").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.empty());
    }
    
    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("╔════════════════════════════════════╗").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("DZTablist v" + plugin.getDescription().getVersion()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("                ║").color(NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("║  ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("By DemonZ Development").color(NamedTextColor.GRAY))
                .append(Component.text("             ║").color(NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("╠════════════════════════════════════╣").color(NamedTextColor.DARK_GRAY));
        
        sendHelpLine(sender, "/dztablist reload", "Reload configurations");
        sendHelpLine(sender, "/dztablist cpu", "Performance statistics");
        sendHelpLine(sender, "/dztablist update", "Manage plugin updates");
        sendHelpLine(sender, "/dztablist credit", "Plugin credits");
        sendHelpLine(sender, "/dztablist help", "This help menu");
        
        sender.sendMessage(Component.text("╚════════════════════════════════════╝").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.empty());
    }
    
    /**
     * Send formatted help line
     */
    private void sendHelpLine(CommandSender sender, String command, String description) {
        // Calculate spacing
        int commandLength = command.length();
        int descLength = description.length();
        int totalContent = commandLength + descLength + 3; // +3 for " - "
        int boxWidth = 36; // Width inside the box
        int spacesNeeded = boxWidth - totalContent;
        if (spacesNeeded < 0) spacesNeeded = 0;
        
        sender.sendMessage(Component.text("║ ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text(command).color(NamedTextColor.AQUA))
                .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(description).color(NamedTextColor.GRAY))
                .append(Component.text(" ".repeat(spacesNeeded) + " ║").color(NamedTextColor.DARK_GRAY)));
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("dztablist.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("reload", "cpu", "update", "credit", "help").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
            List<String> completions = new ArrayList<>(Arrays.asList("check", "latest", "next", "previous"));
            
            // Add version numbers
            UpdateChecker checker = plugin.getUpdateChecker();
            if (checker != null) {
                completions.addAll(checker.getAvailableVersions().stream()
                        .map(UpdateChecker.GitHubRelease::getVersion)
                        .limit(10) // Limit to prevent spam
                        .collect(Collectors.toList()));
            }
            
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}
