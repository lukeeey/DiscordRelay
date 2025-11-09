package dev.lukeeey.discordrelay.nukkit;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import lombok.RequiredArgsConstructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EventListener implements Listener {
    private final DiscordRelayNukkit plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("join", commonPlaceholders(event.getPlayer().getName(), event.getJoinMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("quit", commonPlaceholders(event.getPlayer().getName(), event.getQuitMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        plugin.getPlatform().sendInternalDiscordEventMessage("death", commonPlaceholders(event.getEntity().getName(), event.getDeathMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(PlayerChatEvent event) {
        boolean enabled = plugin.getConfig().getBoolean("relay.server-to-discord.enabled", true);
        boolean replaceAt = plugin.getConfig().getBoolean("relay.server-to-discord.replace-at", true);
        boolean bypassReplaceAt = event.getPlayer().hasPermission("drelay.allowrolepings");
        boolean canSend = event.getPlayer().hasPermission("drelay.sendtodiscord");

        if (enabled && canSend) {
            String message = event.getMessage();

            if (replaceAt && !bypassReplaceAt) {
                message = message.replace("@", "[at]");
            }

            plugin.getPlatform().sendDiscordMessage(TextFormat.clean(plugin.getConfig().getString("relay.server-to-discord.format")
                    .replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                    .replace("{playerName}", event.getPlayer().getName())
                    .replace("{displayName}", event.getPlayer().getDisplayName())
                    .replace("{message}", message)));
        }
    }

    private Map<String, String> commonPlaceholders(String playerName, String defaultMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("{playerName}", playerName);
        map.put("{default}", defaultMessage);
        return map;
    }
}
