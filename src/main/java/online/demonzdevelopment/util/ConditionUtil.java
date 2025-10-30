package online.demonzdevelopment.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for evaluating conditional expressions
 */
public class ConditionUtil {
    
    private static final Pattern CONDITION_PATTERN = Pattern.compile("([^<>=!]+)\\s*([<>=!]+)\\s*([^<>=!]+)");
    
    /**
     * Evaluate a condition string
     * Supports: ==, !=, >, <, >=, <=
     * Example: "%player_health% > 10"
     */
    public static boolean evaluate(Player player, String condition) {
        if (condition == null || condition.isEmpty()) {
            return true;
        }
        
        // Handle AND/OR logic
        if (condition.contains(" AND ")) {
            String[] parts = condition.split(" AND ");
            for (String part : parts) {
                if (!evaluate(player, part.trim())) {
                    return false;
                }
            }
            return true;
        }
        
        if (condition.contains(" OR ")) {
            String[] parts = condition.split(" OR ");
            for (String part : parts) {
                if (evaluate(player, part.trim())) {
                    return true;
                }
            }
            return false;
        }
        
        // Replace placeholders
        String replaced = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, condition);
        
        // Parse condition
        Matcher matcher = CONDITION_PATTERN.matcher(replaced);
        if (!matcher.find()) {
            return false;
        }
        
        String left = matcher.group(1).trim();
        String operator = matcher.group(2).trim();
        String right = matcher.group(3).trim();
        
        // Try numeric comparison
        try {
            double leftNum = Double.parseDouble(left);
            double rightNum = Double.parseDouble(right);
            
            switch (operator) {
                case "==": return leftNum == rightNum;
                case "!=": return leftNum != rightNum;
                case ">": return leftNum > rightNum;
                case "<": return leftNum < rightNum;
                case ">=": return leftNum >= rightNum;
                case "<=": return leftNum <= rightNum;
            }
        } catch (NumberFormatException e) {
            // String comparison
            switch (operator) {
                case "==": return left.equalsIgnoreCase(right);
                case "!=": return !left.equalsIgnoreCase(right);
            }
        }
        
        return false;
    }
    
    /**
     * Evaluate multiple conditions (all must be true)
     */
    public static boolean evaluateAll(Player player, List<String> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        for (String condition : conditions) {
            if (!evaluate(player, condition)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Evaluate multiple conditions (at least one must be true)
     */
    public static boolean evaluateAny(Player player, List<String> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        for (String condition : conditions) {
            if (evaluate(player, condition)) {
                return true;
            }
        }
        return false;
    }
}
