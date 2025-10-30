package online.demonzdevelopment.utils;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for time-based operations
 */
public class TimeUtil {
    
    /**
     * Format milliseconds to readable time string
     */
    public static String formatTime(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    /**
     * Format seconds to readable time string
     */
    public static String formatSeconds(long seconds) {
        return formatTime(seconds * 1000);
    }
}
