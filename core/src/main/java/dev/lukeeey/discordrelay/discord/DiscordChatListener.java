package dev.lukeeey.discordrelay.discord;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.util.ChatColor;
import dev.lukeeey.discordrelay.util.ChatColorConverter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class DiscordChatListener extends ListenerAdapter {
    private final DiscordRelayPlatform platform;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getChannel().equals(platform.getRelayChannel())) return;
        if (event.getAuthor().isBot() && !platform.getAdapter().getConfigBoolean("relay.discord-to-server.allow-bot-messages")) return;

        String message = ChatColor.clean(event.getMessage().getContentStripped());
        String name = ChatColor.clean(event.getMember().getEffectiveName()
                .replace("§", "?")
                .replace("{message}", "?"));
        int maxMessageLength = platform.getAdapter().getConfigInt("relay.discord-to-server.max-message-length");

        if (message.isEmpty()) return;
        if (handleCommands(event.getMember(), event.getChannel().asTextChannel(), message)) return;

        if (!platform.getAdapter().getConfigBoolean("relay.discord-to-server.enabled")) return;
        if (message.length() > maxMessageLength) {
            message = message.substring(0, maxMessageLength);
        }

        Role highestRole = getHighestRole(event.getMember());
        String roleName = highestRole != null ? highestRole.getName() : "";
        String formattedRole = highestRole != null ? getFormattedRoleName(highestRole) : "";

        String response = platform.getAdapter().placeholderApiSupport(platform.getAdapter().getConfigString("relay.discord-to-server.format"));

        platform.getAdapter().broadcastMessage(response.replace("{timestamp}", new Date(System.currentTimeMillis()).toString())
                .replace("{discordUserRole}", roleName)
                .replace("{discordUserRoleColored}", formattedRole)
                .replace("{discordUserId}", event.getMember().getId())
                .replace("{discordUserNickname}", event.getMember().getNickname() != null ? event.getMember().getNickname() : "")
                .replace("{discordUserDiscriminator}", event.getMember().getUser().getDiscriminator())
                .replace("{discordUserName}", name)
                .replace("{message}", message));
    }

    private boolean handleCommands(Member member, TextChannel channel, String message) {
        String commandPrefix = platform.getAdapter().getConfigString("discord-command-prefix");
        List<String> enabledCommands = platform.getAdapter().getConfigStringList("enabled-discord-commands");

        String[] args = message.substring(1).split(" ");
        DiscordCommand command = platform.getDiscordCommand(args[0]);

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
            ChatColor mcColor = ChatColorConverter.fromRGB(role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
            return mcColor + role.getName();
        }
        return role.getName();
    }
}
