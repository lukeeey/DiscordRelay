package io.github.lukeeey.discordrelay.bukkit.discord.defaults;

import io.github.lukeeey.discordrelay.bukkit.DiscordRelayPlugin;
import io.github.lukeeey.discordrelay.bukkit.discord.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

public class PlayerInfoCommand extends DiscordCommand {
    private final DiscordRelayPlugin plugin;

    public PlayerInfoCommand(DiscordRelayPlugin plugin) {
        super("playerinfo", "Display info about a player on the Minecraft server");
        this.plugin = plugin;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        if (args.length == 0) {
            return;
        }
        boolean requestedBy = plugin.getConfig().getBoolean("discord-embed-requested-by");

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            channel.sendMessage(new EmbedBuilder()
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
                .addField("Hunger", ":chicken: " + target.getFoodLevel(), true);

        Date date = new Date(target.getLastPlayed());
        String lastSeen = DateFormat.getInstance().format(date);

        String footer = "Last seen: " + lastSeen;
        if (requestedBy) {
            footer += " | Requested by " + sender.getUser().getName() + "#" + sender.getUser().getDiscriminator();
        }
        builder.setFooter(footer);
        channel.sendMessage(builder.build()).queue();
    }
}
