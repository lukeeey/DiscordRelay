package dev.lukeeey.discordrelay.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EventListener implements Listener {
    private final DiscordRelayBukkit plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("join", commonPlaceholders(event.getPlayer().getName(), event.getJoinMessage()), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("quit", commonPlaceholders(event.getPlayer().getName(), event.getQuitMessage()), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("death", commonPlaceholders(event.getEntity().getName(), event.getDeathMessage()), event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        boolean enabled = plugin.getConfig().getBoolean("relay.server-to-discord.enabled");
        boolean replaceAt = plugin.getConfig().getBoolean("relay.server-to-discord.replace-at");
        boolean opsBypassReplaceAt = plugin.getConfig().getBoolean("relay.server-to-discord.ops-bypass-role-ping-protection");

        if (enabled) {
            String message = event.getMessage();

            if (replaceAt && (!opsBypassReplaceAt || !event.getPlayer().isOp())) {
                message = message.replace("@", "[at]");
            }

            message = ChatColor.stripColor(message);
            String response = plugin.getPlatform().getAdapter().placeholderApiSupport(plugin.getConfig().getString("relay.server-to-discord.format"), event.getPlayer());

            plugin.getPlatform().sendDiscordMessage(response.replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                    .replace("{playerName}", event.getPlayer().getName())
                    .replace("{displayName}", event.getPlayer().getDisplayName())
                    .replace("{message}", message));
        }
    }

    private Map<String, String> commonPlaceholders(String playerName, String defaultMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("{playerName}", playerName);
        map.put("{default}", defaultMessage);
        return map;
    }
}
