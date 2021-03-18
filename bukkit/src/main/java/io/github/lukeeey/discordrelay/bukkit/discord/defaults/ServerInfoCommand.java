package io.github.lukeeey.discordrelay.bukkit.discord.defaults;

import io.github.lukeeey.discordrelay.bukkit.DiscordRelayPlugin;
import io.github.lukeeey.discordrelay.bukkit.discord.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ServerInfoCommand extends DiscordCommand {
    private final DiscordRelayPlugin plugin;

    public ServerInfoCommand(DiscordRelayPlugin plugin) {
        super("serverinfo", "Display info about the Minecraft server");
        this.plugin = plugin;
    }

    @Override
    public void execute(Member sender, TextChannel channel, String[] args) {
        boolean requestedBy = plugin.getConfig().getBoolean("discord-embed-requested-by");

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Server info")
                .addField("Online Players", String.valueOf(plugin.getServer().getOnlinePlayers().size()), false)
                .addField("Minecraft Version", plugin.getServer().getVersion(), false);

        if (requestedBy) {
            builder.setFooter("Requested by " + sender.getUser().getName() + "#" + sender.getUser().getDiscriminator());
        }
        channel.sendMessage(builder.build()).queue();
    }
}
