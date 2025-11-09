package dev.lukeeey.discordrelay;

import dev.lukeeey.discordrelay.util.PlayerData;

import java.util.List;

public interface IDiscordRelayAdapter {
    List<String> getOnlinePlayers();
    int getMaxPlayers();
    String getConfigString(String key);
    String getConfigString(String key, String defaultValue);
    int getConfigInt(String key);
    int getConfigInt(String key, int defaultValue);
    boolean getConfigBoolean(String key);
    List<String> getConfigStringList(String key);
    void broadcastMessage(String message);
    PlayerData getPlayer(String name);
    String getServerVersion();
    void scheduleRepeatingTask(Runnable task, int interval);
    String placeholderApiSupport(String message);
    String placeholderApiSupport(String message, Object playerObject);
}
