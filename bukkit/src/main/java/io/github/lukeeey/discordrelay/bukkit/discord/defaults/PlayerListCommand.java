package io.github.lukeeey.discordrelay.bukkit.discord.defaults;

import io.github.lukeeey.discordrelay.bukkit.DiscordRelayPlugin;
import io.github.lukeeey.discordrelay.bukkit.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PlayerListCommand extends DiscordCommand {
    private final DiscordRelayPlugin plugin;

    public PlayerListCommand(DiscordRelayPlugin plugin) {
        super("playerlist", "Display a list of online players");
        this.plugin = plugin;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        if (plugin.getServer().getOnlinePlayers().isEmpty()) {
            plugin.sendDiscordMessage("**No online players**");
        } else {
            String response = "";
            response += "**Online players (" + plugin.getServer().getOnlinePlayers().size() + "/" + plugin.getServer().getMaxPlayers() + ")**";
            response += "\n```\n";
            response += plugin.getServer().getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
            if (response.length() > 1996) {
                response = response.substring(0, 1993) + "...";
            }
            response += "\n```";
            plugin.sendDiscordMessage(response);
        }
    }
}
