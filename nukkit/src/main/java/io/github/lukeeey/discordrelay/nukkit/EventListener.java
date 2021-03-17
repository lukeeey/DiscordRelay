package io.github.lukeeey.discordrelay.nukkit;

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
    private final DiscordRelayPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.sendInternalDiscordEventMessage("join", commonPlaceholders(event.getPlayer().getName(), event.getJoinMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.sendInternalDiscordEventMessage("quit", commonPlaceholders(event.getPlayer().getName(), event.getQuitMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        plugin.sendInternalDiscordEventMessage("death", commonPlaceholders(event.getEntity().getName(), event.getDeathMessage().getText()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(PlayerChatEvent event) {
        boolean enabled = plugin.getConfig().getBoolean("relay.server-to-discord.enabled");
        boolean replaceAt = plugin.getConfig().getBoolean("relay.server-to-discord.replace-at");
        boolean opsBypassReplaceAt = plugin.getConfig().getBoolean("relay.server-to-discord.ops-bypass-role-ping-protection");

        if (enabled) {
            String message = event.getMessage();

            if (replaceAt && (!opsBypassReplaceAt || !event.getPlayer().isOp())) {
                message = message.replace("@", "[at]");
            }

            plugin.sendDiscordMessage(TextFormat.clean(plugin.getConfig().getString("relay.server-to-discord.format")
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
