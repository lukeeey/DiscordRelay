package io.github.lukeeey.discordrelay.bukkit;

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

@RequiredArgsConstructor
public class EventListener implements Listener {
    private final DiscordRelayPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("relay.events.join.enabled")) {
            plugin.sendDiscordMessage(replaceCommonPlaceholders(plugin.getConfig().getString("relay.events.join.message"),
                    event.getPlayer().getName(), event.getJoinMessage()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("relay.events.quit.enabled")) {
            plugin.sendDiscordMessage(replaceCommonPlaceholders(plugin.getConfig().getString("relay.events.quit.message"),
                    event.getPlayer().getName(), event.getQuitMessage()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getConfig().getBoolean("relay.events.death.enabled")) {
            plugin.sendDiscordMessage(replaceCommonPlaceholders(plugin.getConfig().getString("relay.events.death.message"),
                    event.getEntity().getName(), event.getDeathMessage()));
        }
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

            plugin.sendDiscordMessage(ChatColor.stripColor(plugin.getConfig().getString("relay.server-to-discord.format")
                    .replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                    .replace("{playerName}", event.getPlayer().getName())
                    .replace("{displayName}", event.getPlayer().getDisplayName())
                    .replace("{message}", message)));
        }
    }

    private String replaceCommonPlaceholders(String text, String playerName, String defaultMessage) {
        return text.replace("{playerName}", playerName)
                .replace("{default}", ChatColor.stripColor(defaultMessage));
    }
}
