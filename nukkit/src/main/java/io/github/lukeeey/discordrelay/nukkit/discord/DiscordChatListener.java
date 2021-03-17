package io.github.lukeeey.discordrelay.nukkit.discord;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import io.github.lukeeey.discordrelay.nukkit.DiscordRelayPlugin;
import io.github.lukeeey.discordrelay.nukkit.util.TextFormatConverter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiscordChatListener extends ListenerAdapter {
    private final DiscordRelayPlugin plugin;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (!event.getChannel().equals(plugin.getRelayChannel())) return;
        if (event.getAuthor().isBot() && !plugin.getConfig().getBoolean("relay.discord-to-server.allow-bot-messages")) return;

        String message = TextFormat.clean(event.getMessage().getContentStripped());
        String name = TextFormat.clean(event.getMember().getEffectiveName()
                .replace("§", "?")
                .replace("{message}", "?"));

        if (message.isEmpty()) return;
        if (handleCommands(message)) return;

        if (!plugin.getConfig().getBoolean("relay.discord-to-server.enabled")) return;
        if (message.length() > plugin.getConfig().getInt("relay.discord-to-server.max-message-length")) return;

        Role highestRole = getHighestRole(event.getMember());
        String roleName = highestRole != null ? highestRole.getName() : "";
        String formattedRole = highestRole != null ? getFormattedRoleName(highestRole) : "";

        String response = plugin.getConfig().getString("relay.discord-to-server.format")
                .replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                .replace("{discordRole}", roleName)
                .replace("{discordRoleColored}", formattedRole)
                .replace("{discordName}", name)
                .replace("{message}", message);

        plugin.broadcastMessage(response);
    }

    private boolean handleCommands(String message) {
        String commandPrefix = plugin.getConfig().getString("discord-command-prefix");
        List<String> enabledCommands = plugin.getConfig().getStringList("enabled-discord-commands");

        if (message.equalsIgnoreCase(commandPrefix + "playerlist") && enabledCommands.contains("playerlist")) {
            if (plugin.getServer().getOnlinePlayers().isEmpty()) {
                plugin.sendDiscordMessage("**No online players**");
            } else {
                String response = "";
                response += "**Online players (" + plugin.getServer().getOnlinePlayers().size() + "/" + plugin.getServer().getMaxPlayers() + ")**";
                response += "\n```\n";
                response += plugin.getServer().getOnlinePlayers().values()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));
                if (response.length() > 1996) {
                    response = response.substring(0, 1993) + "...";
                }
                response += "\n```";
                plugin.sendDiscordMessage(response);
            }
            return true;
        }
        return false;
    }

    private Role getHighestRole(Member member) {
        for (Role role : member.getRoles()) {
            return role;
        }
        return null;
    }

    private String getFormattedRoleName(Role role) {
        if (role == null) {
            return "";
        }
        if (role.getColor() != null) {
            TextFormat mcColor = TextFormatConverter.fromRGB(role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
            return mcColor + role.getName();
        }
        return role.getName();
    }
}