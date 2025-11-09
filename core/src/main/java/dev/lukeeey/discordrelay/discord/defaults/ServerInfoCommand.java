package dev.lukeeey.discordrelay.discord.defaults;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.discord.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ServerInfoCommand extends DiscordCommand {
    private final DiscordRelayPlatform platform;

    public ServerInfoCommand(DiscordRelayPlatform platform) {
        super("serverinfo", "Display info about the Minecraft server");
        this.platform = platform;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        boolean requestedBy = platform.getAdapter().getConfigBoolean("discord-embed-requested-by");

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Server info")
                .addField("Online Players", String.valueOf(platform.getAdapter().getOnlinePlayers().size()), true)
                .addField("Max Players", String.valueOf(platform.getAdapter().getMaxPlayers()), true)
                .addField("Minecraft Version", platform.getAdapter().getServerVersion(), true);

        if (requestedBy) {
            builder.setFooter("Requested by " + sender.getUser().getName());
        }
        channel.sendMessageEmbeds(builder.build()).queue();
    }
}
