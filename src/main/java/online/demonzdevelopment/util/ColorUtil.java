package online.demonzdevelopment.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for color and gradient conversions
 */
public class ColorUtil {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    
    /**
     * Convert MiniMessage format to legacy color codes for old clients
     */
    public static String convertToLegacy(String text) {
        try {
            Component component = MINI_MESSAGE.deserialize(text);
            return LegacyComponentSerializer.legacySection().serialize(component);
        } catch (Exception e) {
            // If parsing fails, return original text with hex colors converted
            return convertHexToLegacy(text);
        }
    }
    
    /**
     * Convert hex colors to nearest legacy color
     */
    private static String convertHexToLegacy(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            ChatColor closest = getClosestColor(hex);
            matcher.appendReplacement(buffer, closest.toString());
        }
        matcher.appendTail(buffer);
        
        return buffer.toString();
    }
    
    /**
     * Get closest ChatColor to a hex color
     */
    private static ChatColor getClosestColor(String hex) {
        Color color = Color.decode("#" + hex);
        ChatColor closest = ChatColor.WHITE;
        int minDistance = Integer.MAX_VALUE;
        
        for (ChatColor chatColor : ChatColor.values()) {
            if (chatColor.isFormat()) continue;
            
            Color legacyColor = getLegacyColor(chatColor);
            if (legacyColor == null) continue;
            
            int distance = getColorDistance(color, legacyColor);
            if (distance < minDistance) {
                minDistance = distance;
                closest = chatColor;
            }
        }
        
        return closest;
    }
    
    /**
     * Get RGB color for ChatColor
     */
    private static Color getLegacyColor(ChatColor color) {
        switch (color) {
            case BLACK: return new Color(0, 0, 0);
            case DARK_BLUE: return new Color(0, 0, 170);
            case DARK_GREEN: return new Color(0, 170, 0);
            case DARK_AQUA: return new Color(0, 170, 170);
            case DARK_RED: return new Color(170, 0, 0);
            case DARK_PURPLE: return new Color(170, 0, 170);
            case GOLD: return new Color(255, 170, 0);
            case GRAY: return new Color(170, 170, 170);
            case DARK_GRAY: return new Color(85, 85, 85);
            case BLUE: return new Color(85, 85, 255);
            case GREEN: return new Color(85, 255, 85);
            case AQUA: return new Color(85, 255, 255);
            case RED: return new Color(255, 85, 85);
            case LIGHT_PURPLE: return new Color(255, 85, 255);
            case YELLOW: return new Color(255, 255, 85);
            case WHITE: return new Color(255, 255, 255);
            default: return null;
        }
    }
    
    /**
     * Calculate distance between two colors
     */
    private static int getColorDistance(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return dr * dr + dg * dg + db * db;
    }
    
    /**
     * Parse MiniMessage to Adventure Component
     */
    public static Component parseComponent(String text) {
        try {
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            return Component.text(text);
        }
    }
    
    /**
     * Translate legacy color codes to MiniMessage format
     */
    public static String translateLegacyToMiniMessage(String text) {
        return text.replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
    }
    
    /**
     * Strip all color codes from text
     */
    public static String stripColor(String text) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));
    }
    
    /**
     * Downsample RGB/MiniMessage colors to closest legacy color codes
     * Used for legacy client support (<1.16)
     */
    public static String downsampleToLegacy(String text) {
        if (text == null) return "";
        
        // Use the existing convert methods
        return convertToLegacy(text);
    }
    
    /**
     * Simple downsampling that strips RGB and keeps basic formatting
     */
    public static String downsampleToLegacySimple(String text) {
        if (text == null) return "";
        
        // Remove all hex color codes
        text = text.replaceAll("<#[0-9A-Fa-f]{6}>", "");
        text = text.replaceAll("</#[0-9A-Fa-f]{6}>", "");
        
        // Convert gradients to white
        text = text.replaceAll("<gradient:[^>]+>", "§f");
        text = text.replaceAll("</gradient>", "§r");
        
        // Convert rainbow to light blue
        text = text.replaceAll("<rainbow>", "§b");
        text = text.replaceAll("</rainbow>", "§r");
        
        // Convert basic MiniMessage formatting to legacy codes
        text = text.replace("<bold>", "§l");
        text = text.replace("</bold>", "§r");
        text = text.replace("<italic>", "§o");
        text = text.replace("</italic>", "§r");
        text = text.replace("<underlined>", "§n");
        text = text.replace("</underlined>", "§r");
        text = text.replace("<strikethrough>", "§m");
        text = text.replace("</strikethrough>", "§r");
        text = text.replace("<obfuscated>", "§k");
        text = text.replace("</obfuscated>", "§r");
        
        // Convert basic colors
        text = text.replace("<black>", "§0");
        text = text.replace("<dark_blue>", "§1");
        text = text.replace("<dark_green>", "§2");
        text = text.replace("<dark_aqua>", "§3");
        text = text.replace("<dark_red>", "§4");
        text = text.replace("<dark_purple>", "§5");
        text = text.replace("<gold>", "§6");
        text = text.replace("<gray>", "§7");
        text = text.replace("<dark_gray>", "§8");
        text = text.replace("<blue>", "§9");
        text = text.replace("<green>", "§a");
        text = text.replace("<aqua>", "§b");
        text = text.replace("<red>", "§c");
        text = text.replace("<light_purple>", "§d");
        text = text.replace("<yellow>", "§e");
        text = text.replace("<white>", "§f");
        
        // Remove any remaining MiniMessage tags
        text = text.replaceAll("<[^>]+>", "");
        
        return text;
    }
}
