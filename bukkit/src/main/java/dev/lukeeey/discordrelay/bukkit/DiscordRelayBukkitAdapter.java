package dev.lukeeey.discordrelay.bukkit;

import dev.lukeeey.discordrelay.IDiscordRelayAdapter;
import dev.lukeeey.discordrelay.util.PlayerData;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiscordRelayBukkitAdapter implements IDiscordRelayAdapter {
    private final DiscordRelayBukkit plugin;

    @Override
    public List<String> getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public int getMaxPlayers() {
        return plugin.getServer().getMaxPlayers();
    }

    @Override
    public String getConfigString(String key) {
        return plugin.getConfig().getString(key);
    }

    @Override
    public String getConfigString(String key, String defaultValue) {
        return plugin.getConfig().getString(key, defaultValue);
    }

    @Override
    public int getConfigInt(String key) {
        return plugin.getConfig().getInt(key);
    }

    @Override
    public int getConfigInt(String key, int defaultValue) {
        return plugin.getConfig().getInt(key, defaultValue);
    }

    @Override
    public boolean getConfigBoolean(String key) {
        return plugin.getConfig().getBoolean(key);
    }

    @Override
    public List<String> getConfigStringList(String key) {
        return plugin.getConfig().getStringList(key);
    }

    @Override
    public void broadcastMessage(String message) {
        boolean broadcastToConsole = getConfigBoolean("relay.discord-to-server.broadcast-to-console");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("drelay.receivefromdiscord")) {
                player.sendMessage(message);
            }
            if (broadcastToConsole) {
                plugin.getServer().getLogger().info(message);
            }
        }
    }

    @Override
    public PlayerData getPlayer(String name) {
        Player player = plugin.getServer().getPlayer(name);

        if (player == null) {
            return null;
        }

        Date lastPlayedDate = new Date(player.getLastPlayed());
        String lastSeen = DateFormat.getInstance().format(lastPlayedDate);

        return new PlayerData(
                player.getName(),
                player.getUniqueId(),
                (float) player.getHealth(),
                player.getFoodLevel(),
                lastSeen
        );
    }

    @Override
    public String getServerVersion() {
        return plugin.getServer().getVersion();
    }

    @Override
    public void scheduleRepeatingTask(Runnable task, int interval) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 1, interval);
    }

    @Override
    public String placeholderApiSupport(String message) {
        return placeholderApiSupport(message, null);
    }

    @Override
    public String placeholderApiSupport(String message, Object playerObject) {
        if (!plugin.isPlaceholderApiEnabled()) {
            return message;
        }
        if (!PlaceholderAPI.containsPlaceholders(message)) {
            return message;
        }
        if (playerObject instanceof Player) {
            return PlaceholderAPI.setPlaceholders((Player) playerObject, message);
        }
        return message;
    }

    @Override
    public void logError(String message) {
        plugin.getLogger().severe(message);
    }

    @Override
    public void logInfo(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void logWarning(String message) {
        plugin.getLogger().warning(message);
    }
}
