package online.demonzdevelopment.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import online.demonzdevelopment.DZTablist;
import online.demonzdevelopment.config.ScoreboardConfig;
import online.demonzdevelopment.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scoreboard command handler
 */
public class ScoreboardCommand implements CommandExecutor, TabCompleter {
    
    private final DZTablist plugin;
    
    public ScoreboardCommand(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("dztablist.scoreboard.toggle")) {
            player.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0 || !args[0].equalsIgnoreCase("toggle")) {
            player.sendMessage(Component.text("Usage: /scoreboard toggle").color(NamedTextColor.YELLOW));
            return true;
        }
        
        // Toggle scoreboard
        plugin.getScoreboardManager().toggleScoreboard(player);
        
        // Send message
        ScoreboardConfig config = plugin.getConfigManager().getScoreboardConfig();
        boolean enabled = plugin.getDatabaseManager().isScoreboardEnabled(player.getUniqueId());
        
        String message = enabled ? config.getEnabledMessage() : config.getDisabledMessage();
        player.sendMessage(ColorUtil.parseComponent(message));
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("toggle");
        }
        return new ArrayList<>();
    }
}
