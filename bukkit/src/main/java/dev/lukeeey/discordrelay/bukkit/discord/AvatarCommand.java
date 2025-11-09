package dev.lukeeey.discordrelay.bukkit.discord;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.discord.DiscordCommand;
import dev.lukeeey.discordrelay.util.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class AvatarCommand extends DiscordCommand {
    private final DiscordRelayPlatform platform;

    public AvatarCommand(DiscordRelayPlatform platform) {
        super("avatar", "Display a players head texture");
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
                .setTitle(target.getName() + "'s avatar")
                .setImage("https://crafatar.com/avatars/" + target.getUniqueId().toString());

        if (requestedBy) {
            builder.setFooter(" | Requested by " + sender.getUser().getName());
        }
        channel.sendMessageEmbeds(builder.build()).queue();
    }
}