package io.github.lukeeey.discordrelay.nukkit;

import cn.nukkit.plugin.PluginBase;
import com.google.common.base.Preconditions;
import io.github.lukeeey.discordrelay.nukkit.command.DiscordCommand;
import io.github.lukeeey.discordrelay.nukkit.discord.DiscordChatListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;

public class DiscordRelayPlugin extends PluginBase {
    private JDA jda;

    @Getter
    private TextChannel relayChannel;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("ingame-discord-command-enabled")) {
            getServer().getCommandMap().register("discordrelay", new DiscordCommand(this));
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        try {
            initJDA();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (getConfig().getBoolean("relay.events.server-start.enabled")) {
            sendDiscordMessage(getConfig().getString("relay.events.server-start.message"));
        }
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("relay.events.server-stop.enabled")) {
            sendDiscordMessage(getConfig().getString("relay.events.server-stop.message"));
        }
    }

    private void initJDA() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(getConfig().getString("discord-bot.token")).build();
        jda.awaitReady();
        jda.addEventListener(new DiscordChatListener(this));

        relayChannel = Preconditions.checkNotNull(jda.getTextChannelById(getConfig().getString("discord-bot.channel-id")), "Relay channel cannot be found!");

        if (!getConfig().getString("discord-bot.status").isEmpty()) {
            jda.getPresence().setActivity(Activity.playing(getConfig().getString("discord-bot.status")));
        }

        if (!getConfig().getString("discord-bot.channel-topic").isEmpty()) {
            int interval = getConfig().getInt("discord-bot.channel-topic-update-interval");

            if (interval == -1) {
                updateChannelTopic();
            } else {
                getServer().getScheduler().scheduleRepeatingTask(this, this::updateChannelTopic, 20 * interval);
            }
        }
    }

    private void updateChannelTopic() {
        relayChannel.getManager().setTopic(getConfig().getString("discord-bot.channel-topic")
                .replace("{onlinePlayers}", String.valueOf(getServer().getOnlinePlayers().size()))
                .replace("{maxPlayers}", String.valueOf(getServer().getMaxPlayers())))
                .queue();
    }

    public void sendDiscordMessage(String message) {
        relayChannel.sendMessage(message).queue();
    }

    public void broadcastMessage(String message) {
        if (getConfig().getBoolean("relay.discord-to-server.broadcast-to-console")) {
            getServer().broadcastMessage(message);
        } else {
            getServer().getOnlinePlayers().values().forEach(player -> player.sendMessage(message));
        }
    }
}
