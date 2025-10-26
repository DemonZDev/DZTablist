# DZTablist - Advanced Minecraft Tablist Plugin

<div align="center">

![DZTablist Logo](thumbnail.png)

**The most advanced and customizable tablist plugin for Minecraft**

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/DemonZDevelopment/DZTablist)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.1-green.svg)](https://papermc.io)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

</div>

## 📋 Table of Contents

- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Commands & Permissions](#-commands--permissions)
- [API Usage](#-api-usage)
- [Placeholders](#-placeholders)
- [Performance](#-performance)
- [Support](#-support)

## ✨ Features

### Core Features
- **Header & Footer System** - Customizable header and footer with animations, conditions, and per-world/group/player support
- **Name Tags** - Fully customizable player nametags with prefix, suffix, visibility, and collision rules
- **Sorting System** - Advanced player sorting by group, permission, placeholders (numeric/alphabetic), and custom orders
- **Tablist Formatting** - Complete control over player name display in tablist
- **Playerlist Objective** - Right-side display showing health, custom values, or text
- **Belowname Display** - Show custom values below player names
- **Bossbar System** - Multiple configurable bossbars with animations and conditions
- **Scoreboard System** - Customizable sidebar scoreboards with up to 30 lines
- **Layout System** - Full 80-slot tablist customization (4 columns x 20 rows)
- **Per-World Playerlist** - Show only players from specific worlds
- **Comprehensive API** - Full developer API for integration

### Advanced Features
- **RGB & Gradient Support** - Full MiniMessage formatting with automatic legacy client conversion
- **Placeholder System** - Internal placeholders + full PlaceholderAPI integration including relational placeholders
- **Conditional System** - Show/hide elements based on placeholder values, permissions, and world checks
- **Animation System** - Animated text with configurable frames and intervals
- **Placeholder Replacements** - Modify any placeholder output with find-replace or interval-based logic
- **Multi-Database Support** - YAML, MySQL, or SQLite storage options
- **Performance Optimized** - Fully asynchronous with zero TPS impact
- **Anti-Override Protection** - Prevent other plugins from interfering

## 📦 Requirements

- **Server**: PaperMC 1.21.1+ (or any Paper fork)
- **Java**: 21+
- **Dependencies**:
  - LuckPerms (required)
  - PlaceholderAPI (required)
  - Vault (optional, for additional placeholders)

## 🚀 Installation

1. Download the latest DZTablist.jar from [Releases](https://github.com/DemonZDevelopment/DZTablist/releases)
2. Place the jar file in your server's `plugins` folder
3. Install **LuckPerms** and **PlaceholderAPI** if not already installed
4. Restart your server
5. Configure the plugin in `/plugins/DZTablist/`
6. Run `/dztablist reload` to apply changes

## ⚙️ Configuration

DZTablist uses multiple configuration files for organization:

### Main Configuration Files

| File | Purpose |
|------|---------|
| `config.yml` | Main settings, database, performance |
| `header-footer.yml` | Header and footer configurations |
| `nametags.yml` | Nametag prefix, suffix, visibility |
| `sorting.yml` | Player sorting rules |
| `tablist-format.yml` | Tablist player name formatting |
| `playerlist-objective.yml` | Right-side objective display |
| `belowname.yml` | Below-name value display |
| `bossbar.yml` | Bossbar configurations |
| `scoreboard.yml` | Scoreboard/sidebar configurations |
| `layout.yml` | Custom 80-slot layouts |
| `per-world-playerlist.yml` | Per-world player visibility |
| `placeholders.yml` | Custom placeholders and animations |
| `conditional-placeholders.yml` | Conditional placeholder logic |
| `placeholder-replacements.yml` | Placeholder output replacements |

### Example: Header & Footer Configuration

```yaml
global:
  header:
    - "<gradient:#FF0000:#00FF00>Welcome to the Server</gradient>"
    - ""
    - "<rainbow>Player: %player_name%</rainbow>"
  footer:
    - ""
    - "<gray>Players: <white>%server_online%/%server_max_players%"
    - "<gray>Ping: <white>%player_ping%ms"
  refresh: 1000

groups:
  admin:
    header:
      - "<rainbow><bold>ADMIN PANEL</bold></rainbow>"
    priority: 10
```

### Example: Nametags Configuration

```yaml
groups:
  admin:
    prefix: "&c[Admin] &c"
    suffix: ""
    priority: 90
  
  vip:
    prefix: "&6[VIP] &6"
    suffix: " &6⭐"
    priority: 50
```

### Example: Scoreboard Configuration

```yaml
scoreboards:
  default:
    title: "<gradient:#FF0000:#00FF00><bold>Server Name</bold></gradient>"
    lines:
      - ""
      - "&7Player: &f%player_name%"
      - "&7Rank: &f%vault_rank%"
      - "&7Ping: &f%player_ping%ms"
      - ""
      - "&7Online: &f%server_online%"
      - ""
    refresh: 1000
```

## 🎮 Commands & Permissions

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/dztablist` | Main plugin command | `dztablist.admin` |
| `/dztablist reload` | Reload all configurations | `dztablist.admin` |
| `/dztablist cpu` | Show CPU usage per feature | `dztablist.admin` |
| `/dztablist help` | Show help menu | `dztablist.admin` |
| `/bossbar toggle` | Toggle bossbar visibility | `dztablist.bossbar.toggle` |
| `/scoreboard toggle` | Toggle scoreboard visibility | `dztablist.scoreboard.toggle` |

### Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `dztablist.admin` | Access admin commands | op |
| `dztablist.bossbar.toggle` | Toggle bossbar | true |
| `dztablist.scoreboard.toggle` | Toggle scoreboard | true |
| `dztablist.seeall` | See all players in per-world mode | op |

## 🔧 API Usage

DZTablist provides a comprehensive API for developers to integrate with other plugins.

### Getting Started

Add DZTablist as a dependency in your plugin:

**Maven:**
```xml
<dependency>
    <groupId>online.demonzdevelopment</groupId>
    <artifactId>DZTablist</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```gradle
compileOnly 'online.demonzdevelopment:DZTablist:1.0.0'
```

### API Examples

#### Setting Tablist Header/Footer

```java
import online.demonzdevelopment.api.DZTablistAPI;
import org.bukkit.entity.Player;

public class Example {
    public void setCustomHeader(Player player) {
        DZTablistAPI api = DZTablistAPI.getInstance();
        
        // Set header
        api.setHeader(player, "<gradient:#FF0000:#00FF00>Custom Header</gradient>");
        
        // Set footer
        api.setFooter(player, "<gray>Custom Footer");
    }
}
```

#### Setting Nametag Prefix/Suffix

```java
public void setCustomNametag(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Set prefix
    api.setNameTagPrefix(player, "&c[VIP] &c");
    
    // Set suffix
    api.setNameTagSuffix(player, " &6⭐");
}
```

#### Creating Custom Scoreboard

```java
import java.util.Arrays;
import java.util.List;

public void createCustomScoreboard(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    List<String> lines = Arrays.asList(
        "",
        "&7Player: &f%player_name%",
        "&7Coins: &f%vault_eco_balance%",
        ""
    );
    
    // Create scoreboard
    api.createScoreboard("custom", "<gold>My Scoreboard", lines);
    
    // Show to player
    api.showScoreboard(player, "custom");
}
```

#### Creating Custom Bossbar

```java
import online.demonzdevelopment.api.BossbarColor;
import online.demonzdevelopment.api.BossbarStyle;

public void createCustomBossbar(Player player) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Create bossbar
    api.createBossbar(
        "welcome",
        "<gradient:#FF0000:#00FF00>Welcome!</gradient>",
        1.0f, // Progress (0.0 - 1.0)
        BossbarColor.GREEN,
        BossbarStyle.SOLID
    );
    
    // Show to player
    api.showBossbar(player, "welcome");
    
    // Announce for 10 seconds
    api.announceBossbar(player, "welcome", 10);
}
```

#### Registering Custom Placeholders

```java
import java.util.function.Function;

public void registerPlaceholder() {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Register simple placeholder
    api.registerPlaceholder("myplug in_custom", 1000, player -> {
        return "Custom Value: " + player.getName();
    });
}
```

#### Registering Relational Placeholders

```java
import java.util.function.BiFunction;

public void registerRelationalPlaceholder() {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Register relational placeholder
    api.registerRelationalPlaceholder("myplugin_distance", 1000, (viewer, target) -> {
        double distance = viewer.getLocation().distance(target.getLocation());
        return String.format("%.1f blocks", distance);
    });
}
```

#### Setting Custom Sorting Priority

```java
public void setCustomSorting(Player player, int priority) {
    DZTablistAPI api = DZTablistAPI.getInstance();
    
    // Higher priority = appears first in tablist
    api.setSortingPriority(player, priority);
    
    // Update sorting
    api.updateSorting(player);
}
```

### Full API Reference

```java
public interface DZTablistAPI {
    // Header & Footer
    void setHeader(Player player, String header);
    void setFooter(Player player, String footer);
    
    // Nametags
    void setNameTagPrefix(Player player, String prefix);
    void setNameTagSuffix(Player player, String suffix);
    void setNameTagVisibility(Player player, NameTagVisibility visibility);
    void setNameTagCollisionRule(Player player, CollisionRule rule);
    
    // Tablist Format
    void setTablistPrefix(Player player, String prefix);
    void setTablistName(Player player, String name);
    void setTablistSuffix(Player player, String suffix);
    
    // Sorting
    void setSortingPriority(Player player, int priority);
    void updateSorting(Player player);
    
    // Scoreboard
    void createScoreboard(String id, String title, List<String> lines);
    void showScoreboard(Player player, String id);
    void hideScoreboard(Player player);
    void toggleScoreboard(Player player);
    void announceScoreboard(Player player, String id, int seconds);
    
    // Bossbar
    void createBossbar(String id, String text, float progress, BossbarColor color, BossbarStyle style);
    void showBossbar(Player player, String id);
    void hideBossbar(Player player, String id);
    void toggleBossbar(Player player, String id);
    void announceBossbar(Player player, String id, int seconds);
    void updateBossbarProgress(String id, float progress);
    void updateBossbarText(String id, String text);
    
    // Layout
    void createLayout(String id, Map<Integer, SlotConfig> slots);
    void sendLayout(Player player, String id);
    
    // Placeholders
    void registerPlaceholder(String identifier, int refreshInterval, Function<Player, String> function);
    void registerRelationalPlaceholder(String identifier, int refreshInterval, BiFunction<Player, Player, String> function);
    void unregisterPlaceholder(String identifier);
    
    // Utility
    void reload();
    Map<String, PerformanceStats> getPerformanceStats();
}
```

## 📊 Placeholders

### Internal Placeholders

DZTablist provides internal placeholders for custom use:

| Placeholder | Description |
|-------------|-------------|
| `%animation_<name>%` | Show animated text |
| `%placeholder_<name>%` | Show custom placeholder |
| `%conditional_<name>%` | Show conditional placeholder |
| `%rel_<name>_<target>%` | Show relational placeholder |

### PlaceholderAPI Integration

All PlaceholderAPI placeholders are supported, including:

- Player placeholders: `%player_name%`, `%player_health%`, `%player_level%`, etc.
- Server placeholders: `%server_online%`, `%server_max_players%`, `%server_tps%`, etc.
- Vault placeholders: `%vault_rank%`, `%vault_eco_balance%`, etc.
- **Relational placeholders**: `%rel_<identifier>%` (between two players)

### Custom Placeholder Examples

#### Define Animation
```yaml
# placeholders.yml
animations:
  loading:
    frames:
      - "Loading."
      - "Loading.."
      - "Loading..."
    interval: 500
```

Usage: `%animation_loading%`

#### Define Conditional
```yaml
# conditional-placeholders.yml
conditionals:
  health-bar:
    conditions:
      - condition: "%player_health% >= 15"
        output: "&a■■■■■■■■■■"
      - condition: "%player_health% >= 10"
        output: "&e■■■■■&7■■■■■"
    default: "&c■■&7■■■■■■■■"
```

Usage: `%conditional_health-bar%`

## ⚡ Performance

DZTablist is designed for maximum performance:

- **Zero TPS Impact**: All operations run asynchronously
- **Smart Caching**: Placeholder values cached with configurable TTL
- **Packet Batching**: Reduces network overhead
- **Configurable Refresh Rates**: Customize update frequency per feature
- **Feature Toggles**: Disable unused features to reduce overhead
- **Optimized for Thousands**: Handles thousands of concurrent players efficiently

### Performance Monitoring

Check CPU usage per feature:
```
/dztablist cpu
```

Output example:
```
════════════════════════════════════
  DZTablist Performance Monitor
════════════════════════════════════
Header/Footer: 0.12ms avg (1,234 calls)
Nametags: 0.08ms avg (2,456 calls)
Scoreboard: 0.15ms avg (987 calls)
Bossbar: 0.05ms avg (654 calls)
Sorting: 0.20ms avg (321 calls)
════════════════════════════════════
Total: 0.60ms avg | 0% CPU usage
════════════════════════════════════
```

## 🎨 MiniMessage Format

DZTablist uses MiniMessage for text formatting. Examples:

```yaml
# RGB Colors
"<#FF5555>Red Text"
"<color:#00FF00>Green Text"

# Gradients
"<gradient:#FF0000:#0000FF>Rainbow Text</gradient>"
"<gradient:#FFFF00:#FF00FF>Custom Gradient</gradient>"

# Rainbow
"<rainbow>Rainbow Text</rainbow>"
"<rainbow:!>Rainbow without colors</rainbow>"

# Formatting
"<bold>Bold Text</bold>"
"<italic>Italic Text</italic>"
"<underlined>Underlined</underlined>"
"<strikethrough>Strike</strikethrough>"
"<obfuscated>Obfuscated</obfuscated>"

# Combined
"<gradient:#FF0000:#00FF00><bold>Gradient Bold</bold></gradient>"
```

For full MiniMessage documentation: [MiniMessage Docs](https://docs.advntr.dev/minimessage/format.html)

## 🐛 Troubleshooting

### Common Issues

**Issue**: "LuckPerms not found"
- **Solution**: Install LuckPerms on your server

**Issue**: "PlaceholderAPI not found"
- **Solution**: Install PlaceholderAPI on your server

**Issue**: Placeholders not working
- **Solution**: Ensure PlaceholderAPI expansions are installed for used placeholders

**Issue**: Colors not showing on older clients
- **Solution**: RGB colors automatically downgrade to legacy colors for <1.16 clients

**Issue**: Nametags not updating
- **Solution**: Check anti-override settings and ensure no conflicting plugins

### Debug Mode

Enable debug mode in `config.yml`:
```yaml
logging:
  debug-mode: true
```

This will log detailed information about all operations.

### Error Logs

All errors are logged to `/plugins/DZTablist/errors.log` with full stack traces.

## 📞 Support

- **Discord**: [Join our Discord](https://discord.gg/example)
- **Issues**: [GitHub Issues](https://github.com/DemonZDevelopment/DZTablist/issues)
- **Wiki**: [Documentation Wiki](https://github.com/DemonZDevelopment/DZTablist/wiki)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- **Author**: DemonZ Development
- **Contributors**: [List of contributors](https://github.com/DemonZDevelopment/DZTablist/graphs/contributors)
- **Libraries**: PaperMC, LuckPerms, PlaceholderAPI, Adventure, MiniMessage

## 🔄 Changelog

### Version 1.0.0 (Initial Release)
- ✨ Initial release with all core features
- 📝 Complete header/footer system
- 🏷️ Advanced nametag management
- 📊 Comprehensive scoreboard system
- 🎯 Bossbar support
- 🔧 Full API for developers
- ⚡ Performance optimized
- 🎨 MiniMessage & RGB support
- 📦 Multi-database support

---

<div align="center">

**Made with ❤️ by DemonZ Development**

[⬆ Back to Top](#dztablist---advanced-minecraft-tablist-plugin)

</div>

