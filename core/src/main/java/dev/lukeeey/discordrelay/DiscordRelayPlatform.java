package dev.lukeeey.discordrelay;

import dev.lukeeey.discordrelay.discord.DiscordChatListener;
import dev.lukeeey.discordrelay.discord.DiscordCommand;
import dev.lukeeey.discordrelay.discord.defaults.PlayerInfoCommand;
import dev.lukeeey.discordrelay.discord.defaults.PlayerListCommand;
import dev.lukeeey.discordrelay.discord.defaults.ServerInfoCommand;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DiscordRelayPlatform {
    @Getter
    private final IDiscordRelayAdapter adapter;

    @Getter
    private final Map<String, DiscordCommand> discordCommands = new HashMap<>();

    @Getter
    private JDA jda;

    @Getter
    private TextChannel relayChannel;

    public DiscordRelayPlatform(IDiscordRelayAdapter adapter) {
        this.adapter = adapter;
    }

    public void registerCommands() {
        registerDiscordCommand(new PlayerListCommand(this));
        registerDiscordCommand(new ServerInfoCommand(this));
        registerDiscordCommand(new PlayerInfoCommand(this));
    }

    public void initJDA() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(adapter.getConfigString("discord-bot.token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new DiscordChatListener(this))
                .setEnableShutdownHook(true)
                .build();

        jda.awaitReady();

        relayChannel = jda.getTextChannelById(adapter.getConfigString("discord-bot.channel-id"));

        if (!adapter.getConfigString("discord-bot.status").isEmpty()) {
            jda.getPresence().setActivity(Activity.playing(adapter.placeholderApiSupport(adapter.getConfigString("discord-bot.status"))));
        }

        if (!adapter.getConfigString("discord-bot.channel-topic").isEmpty()) {
            int interval = adapter.getConfigInt("discord-bot.channel-topic-update-interval");

            if (interval == -1) {
                updateChannelTopic();
            } else {
                adapter.scheduleRepeatingTask(this::updateChannelTopic, 20 * interval);
            }
        }
    }

    private void updateChannelTopic() {
        relayChannel.getManager().setTopic(adapter.placeholderApiSupport(adapter.getConfigString("discord-bot.channel-topic")
                        .replace("{onlinePlayers}", String.valueOf(adapter.getOnlinePlayers().size()))
                        .replace("{maxPlayers}", String.valueOf(adapter.getMaxPlayers()))))
                .queue();
    }

    /**
     * Send a message to the Discord relay channel.
     *
     * @param message the message to send
     */
    public void sendDiscordMessage(String message) {
        if (relayChannel != null) {
            relayChannel.sendMessage(message).queue();
        }
    }

    /**
     * Send an embed to the Discord relay channel.
     *
     * @param embed the embed to send
     */
    public void sendDiscordMessage(MessageEmbed embed) {
        if (relayChannel != null) {
            relayChannel.sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Register a command that can be executed in the Discord relay channel.
     * The name should not contain the prefix as this is specified in the config.
     *
     * @param command the discord command to register
     */
    public void registerDiscordCommand(DiscordCommand command) {
        discordCommands.put(command.getName(), command);
    }

    /**
     * Get a currently registered Discord command.
     *
     * @param name the name of the command
     * @return the command class if present, else null
     */
    public DiscordCommand getDiscordCommand(String name) {
        return discordCommands.get(name);
    }

    public void sendInternalDiscordEventMessage(String configKey) {
        sendInternalDiscordEventMessage(configKey, Collections.emptyMap());
    }

    public void sendInternalDiscordEventMessage(String configKey, Map<String, String> placeholders) {
        sendInternalDiscordEventMessage(configKey, Collections.emptyMap(), null);
    }

    public void sendInternalDiscordEventMessage(String configKey, Map<String, String> placeholders, Object playerObject) {
        boolean enabled = adapter.getConfigBoolean("relay.events." + configKey + ".enabled");

        if (enabled) {
            String message = adapter.getConfigString("relay.events." + configKey + ".message");
            boolean showEmbed = adapter.getConfigBoolean("relay.events." + configKey + ".embed");
            String embedColor = adapter.getConfigString("relay.events." + configKey + ".embed-color");

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }

            message = adapter.placeholderApiSupport(message, playerObject);

            if (showEmbed) {
                sendDiscordMessage(new EmbedBuilder()
                        .setTitle(message)
                        .setColor(embedColor.isEmpty() || embedColor.equalsIgnoreCase("default") ? null : Color.decode(embedColor))
                        .build());
            } else {
                sendDiscordMessage(message);
            }
        }
    }
}
