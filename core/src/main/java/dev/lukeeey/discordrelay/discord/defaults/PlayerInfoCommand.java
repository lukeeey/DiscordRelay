package dev.lukeeey.discordrelay.discord.defaults;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.discord.DiscordCommand;
import dev.lukeeey.discordrelay.util.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class PlayerInfoCommand extends DiscordCommand {
    private final DiscordRelayPlatform platform;

    public PlayerInfoCommand(DiscordRelayPlatform platform) {
        super("playerinfo", "Display info about a player on the Minecraft server");
        this.platform = platform;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        if (args.length == 0) {
            return;
        }
        boolean requestedBy = platform.getAdapter().getConfigBoolean("discord-embed-requested-by");

        PlayerData target = platform.getAdapter().getPlayer(args[0]);
        if (target == null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(Color.RED)
                    .setDescription("Player \"" + args[0] + "\" was not found")
                    .build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Player info")
                .addField("Name", target.getName(), true)
                .addField("Health", ":heart: " + target.getHealth(), true)
                .addField("Hunger", ":chicken: " + target.getFood(), true);

        String footer = "Last seen: " + target.getLastSeen();
        if (requestedBy) {
            footer += " | Requested by " + sender.getUser().getName() + "#" + sender.getUser().getDiscriminator();
        }
        builder.setFooter(footer);
        channel.sendMessageEmbeds(builder.build()).queue();
    }
}