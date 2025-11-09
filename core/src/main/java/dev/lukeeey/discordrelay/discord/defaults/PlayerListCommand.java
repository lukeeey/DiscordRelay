package dev.lukeeey.discordrelay.discord.defaults;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.stream.Collectors;

public class PlayerListCommand extends DiscordCommand {
    private final DiscordRelayPlatform platform;

    public PlayerListCommand(DiscordRelayPlatform platform) {
        super("playerlist", "Display a list of online players");
        this.platform = platform;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        if (platform.getAdapter().getOnlinePlayers().isEmpty()) {
            platform.sendDiscordMessage("**No online players**");
        } else {
            String response = "";
            response += "**Online players (" + platform.getAdapter().getOnlinePlayers().size() + "/" + platform.getAdapter().getMaxPlayers() + ")**";
            response += "\n```\n";
            response += String.join(", ", platform.getAdapter().getOnlinePlayers());
            if (response.length() > 1996) {
                response = response.substring(0, 1993) + "...";
            }
            response += "\n```";
            platform.sendDiscordMessage(response);
        }
    }
}
