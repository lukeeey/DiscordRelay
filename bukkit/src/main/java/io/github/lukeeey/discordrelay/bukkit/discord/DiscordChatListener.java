package io.github.lukeeey.discordrelay.bukkit.discord;

import io.github.lukeeey.discordrelay.bukkit.DiscordRelayPlugin;
import io.github.lukeeey.discordrelay.bukkit.util.TextFormatConverter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class DiscordChatListener extends ListenerAdapter {
    private final DiscordRelayPlugin plugin;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (!event.getChannel().equals(plugin.getRelayChannel())) return;
        if (event.getAuthor().isBot() && !plugin.getConfig().getBoolean("relay.discord-to-server.allow-bot-messages")) return;

        String message = ChatColor.stripColor(event.getMessage().getContentStripped());
        String name = ChatColor.stripColor(event.getMember().getEffectiveName()
                .replace("§", "?")
                .replace("{message}", "?"));
        int maxMessageLength = plugin.getConfig().getInt("relay.discord-to-server.max-message-length");

        if (message.isEmpty()) return;
        if (handleCommands(event.getMember(), event.getChannel(), message)) return;

        if (!plugin.getConfig().getBoolean("relay.discord-to-server.enabled")) return;
        if (message.length() > maxMessageLength) {
            message = message.substring(0, maxMessageLength);
        }

        Role highestRole = getHighestRole(event.getMember());
        String roleName = highestRole != null ? highestRole.getName() : "";
        String formattedRole = highestRole != null ? getFormattedRoleName(highestRole) : "";

        String response = plugin.getConfig().getString("relay.discord-to-server.format")
                .replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                .replace("{discordRole}", roleName)
                .replace("{discordRoleColored}", formattedRole)
                .replace("{discordName}", name)
                .replace("{message}", message);

        plugin.broadcastMessage(PlaceholderAPI.setPlaceholders(null, response));
    }

    private boolean handleCommands(Member member, TextChannel channel, String message) {
        String commandPrefix = plugin.getConfig().getString("discord-command-prefix");
        List<String> enabledCommands = plugin.getConfig().getStringList("enabled-discord-commands");

        String[] args = message.substring(1).split(" ");
        DiscordCommand command = plugin.getDiscordCommand(args[0]);

        if (message.startsWith(commandPrefix) && command != null && enabledCommands.contains(command.getName())) {
            command.execute(member, channel, Arrays.copyOfRange(args, 1, args.length));
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
            ChatColor mcColor = TextFormatConverter.fromRGB(role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
            return mcColor + role.getName();
        }
        return role.getName();
    }
}
