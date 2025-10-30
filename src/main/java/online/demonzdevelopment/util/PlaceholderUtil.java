package online.demonzdevelopment.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import online.demonzdevelopment.DZTablist;
import org.bukkit.entity.Player;

/**
 * Utility class for placeholder operations
 */
public class PlaceholderUtil {
    
    private final DZTablist plugin;
    
    public PlaceholderUtil(DZTablist plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Replace all placeholders in text for a player
     */
    public static String replacePlaceholders(Player player, String text) {
        if (text == null) return "";
        
        // Replace PlaceholderAPI placeholders
        if (DZTablist.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        
        // Replace internal placeholders
        text = DZTablist.getInstance().getPlaceholderManager().replace(player, text);
        
        // Translate color codes
        text = ColorUtil.translateLegacyToMiniMessage(text);
        
        return text;
    }
    
    /**
     * Replace relational placeholders between two players
     */
    public static String replaceRelationalPlaceholders(Player viewer, Player target, String text) {
        if (text == null) return "";
        
        // Replace PlaceholderAPI relational placeholders
        if (DZTablist.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setRelationalPlaceholders(viewer, target, text);
        }
        
        // Replace internal placeholders
        text = DZTablist.getInstance().getPlaceholderManager().replaceRelational(viewer, target, text);
        
        // Translate color codes
        text = ColorUtil.translateLegacyToMiniMessage(text);
        
        return text;
    }
    
    /**
     * Parse a numeric placeholder value
     */
    public static double parseNumericPlaceholder(Player player, String placeholder) {
        String value = replacePlaceholders(player, placeholder);
        try {
            return Double.parseDouble(ColorUtil.stripColor(value));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
