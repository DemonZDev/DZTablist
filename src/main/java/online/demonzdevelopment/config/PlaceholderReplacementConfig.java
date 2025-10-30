package online.demonzdevelopment.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Placeholder replacement configuration handler
 */
public class PlaceholderReplacementConfig {
    
    private final FileConfiguration config;
    
    public PlaceholderReplacementConfig(FileConfiguration config) {
        this.config = config;
    }
    
    public Map<String, ReplacementData> getReplacements() {
        Map<String, ReplacementData> replacements = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("replacements");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String placeholder = section.getString(key + ".placeholder", "");
                List<TextReplacement> textReplacements = new ArrayList<>();
                List<NumberInterval> numberIntervals = new ArrayList<>();
                String format = section.getString(key + ".format", "$original");
                String defaultOutput = section.getString(key + ".default", "");
                
                // Text replacements
                ConfigurationSection replSection = section.getConfigurationSection(key + ".replacements");
                if (replSection != null) {
                    for (String replKey : replSection.getKeys(false)) {
                        String find = replSection.getString(replKey + ".find", "");
                        String replace = replSection.getString(replKey + ".replace", "");
                        textReplacements.add(new TextReplacement(find, replace));
                    }
                }
                
                // Number intervals
                ConfigurationSection intSection = section.getConfigurationSection(key + ".number-intervals");
                if (intSection != null) {
                    for (String intKey : intSection.getKeys(false)) {
                        double min = intSection.getDouble(intKey + ".min", 0);
                        double max = intSection.getDouble(intKey + ".max", 0);
                        String output = intSection.getString(intKey + ".output", "");
                        numberIntervals.add(new NumberInterval(min, max, output));
                    }
                }
                
                replacements.put(key, new ReplacementData(placeholder, textReplacements, numberIntervals, format, defaultOutput));
            }
        }
        return replacements;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public static class ReplacementData {
        private final String placeholder;
        private final List<TextReplacement> textReplacements;
        private final List<NumberInterval> numberIntervals;
        private final String format;
        private final String defaultOutput;
        
        public ReplacementData(String placeholder, List<TextReplacement> textReplacements, List<NumberInterval> numberIntervals, String format, String defaultOutput) {
            this.placeholder = placeholder;
            this.textReplacements = textReplacements;
            this.numberIntervals = numberIntervals;
            this.format = format;
            this.defaultOutput = defaultOutput;
        }
        
        public String getPlaceholder() { return placeholder; }
        public List<TextReplacement> getTextReplacements() { return textReplacements; }
        public List<NumberInterval> getNumberIntervals() { return numberIntervals; }
        public String getFormat() { return format; }
        public String getDefaultOutput() { return defaultOutput; }
    }
    
    public static class TextReplacement {
        private final String find;
        private final String replace;
        
        public TextReplacement(String find, String replace) {
            this.find = find;
            this.replace = replace;
        }
        
        public String getFind() { return find; }
        public String getReplace() { return replace; }
    }
    
    public static class NumberInterval {
        private final double min;
        private final double max;
        private final String output;
        
        public NumberInterval(double min, double max, String output) {
            this.min = min;
            this.max = max;
            this.output = output;
        }
        
        public double getMin() { return min; }
        public double getMax() { return max; }
        public String getOutput() { return output; }
    }
}
