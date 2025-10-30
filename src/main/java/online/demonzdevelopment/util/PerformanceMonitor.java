package online.demonzdevelopment.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Monitors performance and CPU usage per feature
 */
public class PerformanceMonitor {
    
    private final Map<String, Long> featureTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> featureCalls = new ConcurrentHashMap<>();
    private final Map<String, Long> featureStartTimes = new ConcurrentHashMap<>();
    
    /**
     * Start timing a feature
     */
    public void startTiming(String feature) {
        featureStartTimes.put(Thread.currentThread().getName() + "_" + feature, System.nanoTime());
    }
    
    /**
     * Stop timing a feature
     */
    public void stopTiming(String feature) {
        String key = Thread.currentThread().getName() + "_" + feature;
        Long startTime = featureStartTimes.remove(key);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            featureTimes.merge(feature, duration, Long::sum);
            featureCalls.merge(feature, 1L, Long::sum);
        }
    }
    
    /**
     * Get average execution time for a feature in milliseconds
     */
    public double getAverageTime(String feature) {
        Long totalTime = featureTimes.get(feature);
        Long calls = featureCalls.get(feature);
        if (totalTime == null || calls == null || calls == 0) {
            return 0.0;
        }
        return (totalTime / calls) / 1_000_000.0; // Convert to ms
    }
    
    /**
     * Get total execution time for a feature in milliseconds
     */
    public double getTotalTime(String feature) {
        Long totalTime = featureTimes.get(feature);
        if (totalTime == null) {
            return 0.0;
        }
        return totalTime / 1_000_000.0; // Convert to ms
    }
    
    /**
     * Get total calls for a feature
     */
    public long getTotalCalls(String feature) {
        return featureCalls.getOrDefault(feature, 0L);
    }
    
    /**
     * Get all feature statistics
     */
    public Map<String, FeatureStats> getAllStats() {
        Map<String, FeatureStats> stats = new HashMap<>();
        for (String feature : featureTimes.keySet()) {
            stats.put(feature, new FeatureStats(
                getTotalCalls(feature),
                getTotalTime(feature),
                getAverageTime(feature)
            ));
        }
        return stats;
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        featureTimes.clear();
        featureCalls.clear();
        featureStartTimes.clear();
    }
    
    /**
     * Feature statistics container
     */
    public static class FeatureStats {
        private final long calls;
        private final double totalTime;
        private final double averageTime;
        
        public FeatureStats(long calls, double totalTime, double averageTime) {
            this.calls = calls;
            this.totalTime = totalTime;
            this.averageTime = averageTime;
        }
        
        public long getCalls() { return calls; }
        public double getTotalTime() { return totalTime; }
        public double getAverageTime() { return averageTime; }
    }
}
