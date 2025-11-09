package dev.lukeeey.discordrelay.nukkit;

import cn.nukkit.Player;
import dev.lukeeey.discordrelay.IDiscordRelayAdapter;
import dev.lukeeey.discordrelay.util.PlayerData;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiscordRelayNukkitAdapter implements IDiscordRelayAdapter {
    private final DiscordRelayNukkit plugin;

    @Override
    public List<String> getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers().values().stream().map(Player::getName).collect(Collectors.toList());
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
        if (plugin.getConfig().getBoolean("relay.discord-to-server.broadcast-to-console")) {
            plugin.getServer().broadcastMessage(message);
        } else {
            plugin.getServer().getOnlinePlayers().values().forEach(player -> player.sendMessage(message));
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
                player.getHealth(),
                player.getFoodData().getLevel(),
                lastSeen
        );
    }

    @Override
    public String getServerVersion() {
        return plugin.getServer().getVersion();
    }

    @Override
    public void scheduleRepeatingTask(Runnable task, int interval) {
        plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, task, interval);
    }

    @Override
    public String placeholderApiSupport(String message) {
        return message;
    }

    @Override
    public String placeholderApiSupport(String message, Object playerObject) {
        return message;
    }
}
