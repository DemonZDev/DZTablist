# API Usage Examples

This document provides comprehensive examples for using the DZTablist API.

## Setup

Add DZTablist as a dependency in your `plugin.yml`:
```yaml
depend: [DZTablist]
```

Import the API in your code:
```java
import online.demonzdevelopment.api.DZTablistAPI;
```

## Examples

### 1. Setting Custom Header/Footer

```java
import online.demonzdevelopment.api.DZTablistAPI;
import org.bukkit.entity.Player;

public class MyPlugin {
    public void setCustomHeaderFooter(Player player) {
        DZTablistAPI api = DZTablistAPI.getInstance();
        
        // Set custom header
        api.setHeader(player, "<gradient:#FF0000:#00FF00>Welcome " + player.getName() + "!</gradient>");
        
        // Set custom footer
        api.setFooter(player, "<gray>Enjoy your stay!");
    }
}
```

### 2. Setting Custom Nametags

```java
public void setVIPNametag(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Set prefix
    api.setNameTagPrefix(player, "&6[VIP] &6");
    
    // Set suffix
    api.setNameTagSuffix(player, " &6тнР");
}
```

### 3. Custom Sorting Priority

```java
public void setCustomPriority(Player player, String rank) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    int priority = switch (rank) {
        case "owner" -> 1000;
        case "admin" -> 900;
        case "mod" -> 800;
        case "vip" -> 500;
        default -> 1;
    };
    
    api.setSortingPriority(player, priority);
    api.updateSorting(player);
}
```

### 4. Creating Custom Scoreboard

```java
import java.util.Arrays;
import java.util.List;

public void showCustomScoreboard(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    List<String> lines = Arrays.asList(
        "",
        "&7Player: &f%player_name%",
        "&7Rank: &f%vault_rank%",
        "&7Coins: &f%vault_eco_balance%",
        "",
        "&7Server: &fexample.com",
        ""
    );
    
    // Create scoreboard
    api.createScoreboard("myscoreboard", "<gold><bold>My Server</bold></gold>", lines);
    
    // Show to player
    api.showScoreboard(player, "myscoreboard");
}
```

### 5. Announcing Scoreboard

```java
public void announceEvent(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    List<String> lines = Arrays.asList(
        "",
        "&6&lEVENT STARTING",
        "",
        "&7Join now!",
        "&7/event join",
        ""
    );
    
    api.createScoreboard("event", "<gold><bold>EVENT</bold></gold>", lines);
    
    // Show for 10 seconds
    api.announceScoreboard(player, "event", 10);
}
```

### 6. Creating Custom Bossbar

```java
import online.demonzdevelopment.api.BossbarColor;
import online.demonzdevelopment.api.BossbarStyle;

public void showWelcomeBossbar(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Create bossbar
    api.createBossbar(
        "welcome",
        "<gradient:#FF0000:#00FF00>Welcome to the server!</gradient>",
        1.0f, // Full progress
        BossbarColor.GREEN,
        BossbarStyle.SOLID
    );
    
    // Show to player
    api.showBossbar(player, "welcome");
}
```

### 7. Announcing Bossbar

```java
public void announceServerRestart(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    api.createBossbar(
        "restart",
        "<red><bold>Server restarting in 60 seconds!</bold></red>",
        1.0f,
        BossbarColor.RED,
        BossbarStyle.SEGMENTED_10
    );
    
    // Show for 60 seconds
    api.announceBossbar(player, "restart", 60);
}
```

### 8. Registering Custom Placeholders

```java
import java.util.function.Function;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DZTablistAPI api = DZTablistAPI.getInstance();
        
        // Register simple placeholder
        api.registerPlaceholder("myplugin_level", 1000, player -> {
            // Your logic to get player level
            int level = getPlayerLevel(player);
            return String.valueOf(level);
        });
        
        // Register placeholder with formatting
        api.registerPlaceholder("myplugin_coins", 1000, player -> {
            double coins = getPlayerCoins(player);
            return String.format("$%.2f", coins);
        });
    }
    
    private int getPlayerLevel(Player player) {
        // Your implementation
        return 1;
    }
    
    private double getPlayerCoins(Player player) {
        // Your implementation
        return 0.0;
    }
}
```

### 9. Registering Relational Placeholders

```java
import java.util.function.BiFunction;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DZTablistAPI api = DZTablistAPI.getInstance();
        
        // Register relational placeholder
        api.registerRelationalPlaceholder("myplugin_friend_status", 2000, (viewer, target) -> {
            if (areFriends(viewer, target)) {
                return "<green>Friend</green>";
            } else {
                return "<gray>Not Friends</gray>";
            }
        });
        
        // Distance placeholder
        api.registerRelationalPlaceholder("myplugin_distance", 1000, (viewer, target) -> {
            double distance = viewer.getLocation().distance(target.getLocation());
            return String.format("%.1f blocks", distance);
        });
    }
    
    private boolean areFriends(Player p1, Player p2) {
        // Your implementation
        return false;
    }
}
```

### 10. Performance Monitoring

```java
import online.demonzdevelopment.api.DZTablistAPI;
import java.util.Map;

public void checkPerformance() {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    Map<String, DZTablistAPI.PerformanceStats> stats = api.getPerformanceStats();
    
    for (Map.Entry<String, DZTablistAPI.PerformanceStats> entry : stats.entrySet()) {
        String feature = entry.getKey();
        DZTablistAPI.PerformanceStats stat = entry.getValue();
        
        System.out.println(feature + ": " + 
            stat.getAverageTime() + "ms avg (" + 
            stat.getCalls() + " calls)");
    }
}
```

### 11. Full Integration Example

```java
import online.demonzdevelopment.api.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class MyIntegration extends JavaPlugin implements Listener {
    
    private DZTablistAPI api;
    
    @Override
    public void onEnable() {
        api = DZTablistAPI.getInstance();
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register custom placeholders
        registerPlaceholders();
    }
    
    private void registerPlaceholders() {
        // Custom placeholder
        api.registerPlaceholder("myserver_rank", 1000, player -> {
            return getRank(player);
        });
        
        // Relational placeholder
        api.registerRelationalPlaceholder("myserver_relationship", 2000, (viewer, target) -> {
            return getRelationship(viewer, target);
        });
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Set custom header/footer
        api.setHeader(player, "<gradient:#FF0000:#00FF00>Welcome!</gradient>");
        api.setFooter(player, "<gray>Enjoy your stay!");
        
        // Set nametag
        String rank = getRank(player);
        api.setNameTagPrefix(player, "&6[" + rank + "] &6");
        
        // Show welcome bossbar
        api.createBossbar("welcome", 
            "<gold>Welcome " + player.getName() + "!</gold>",
            1.0f, BossbarColor.GOLD, BossbarStyle.SOLID);
        api.announceBossbar(player, "welcome", 5);
        
        // Show custom scoreboard
        List<String> lines = Arrays.asList(
            "",
            "&7Welcome!",
            "&7Player: &f" + player.getName(),
            ""
        );
        api.createScoreboard("welcome", "<gold>Welcome</gold>", lines);
        api.announceScoreboard(player, "welcome", 10);
    }
    
    private String getRank(Player player) {
        // Your implementation
        return "Player";
    }
    
    private String getRelationship(Player viewer, Player target) {
        // Your implementation
        return "Neutral";
    }
}
```

## Usage in Placeholders

After registering placeholders, you can use them in any DZTablist config:

```yaml
# header-footer.yml
global:
  header:
    - "%placeholder_myplugin_level%"
    - "%placeholder_myserver_rank%"
```

For relational placeholders:
```yaml
# Use in tablist format
tablist-format:
  global:
    suffix: "%rel_myserver_relationship%"
```

## Best Practices

1. **Register placeholders in onEnable()** - Ensure they're available when DZTablist loads
2. **Use appropriate refresh intervals** - Don't update too frequently (1000ms minimum recommended)
3. **Handle null values** - Always check for null players in your placeholder functions
4. **Unregister on disable** - Clean up your placeholders when your plugin disables
5. **Test performance** - Use the performance monitoring API to ensure your placeholders aren't causing lag

## Support

For more examples and support, visit:
- GitHub: https://github.com/DemonZDevelopment/DZTablist
- Discord: https://discord.gg/example
