package io.github.lukeeey.discordrelay.bukkit;

import com.google.common.base.Preconditions;
import io.github.lukeeey.discordrelay.bukkit.discord.DiscordChatListener;
import io.github.lukeeey.discordrelay.bukkit.discord.DiscordCommand;
import io.github.lukeeey.discordrelay.bukkit.discord.defaults.PlayerListCommand;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DiscordRelayPlugin extends JavaPlugin implements CommandExecutor {
    private final Map<String, DiscordCommand> discordCommands = new HashMap<>();

    private JDA jda;

    @Getter
    private TextChannel relayChannel;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("ingame-discord-command-enabled")) {
            getCommand("discord").setExecutor(this);
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        try {
            initJDA();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        if (getConfig().getStringList("enabled-discord-commands").contains("playerlist")) {
            discordCommands.put("playerlist", new PlayerListCommand(this));
        }
        sendInternalDiscordEventMessage("server-start");
    }

    @Override
    public void onDisable() {
        sendInternalDiscordEventMessage("server-stop");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discord")) {
            sender.sendMessage(getConfig().getString("ingame-discord-command-response"));
        }
        return true;
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
                getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateChannelTopic, 0, 20 * interval);
            }
        }
    }

    private void updateChannelTopic() {
        relayChannel.getManager().setTopic(getConfig().getString("discord-bot.channel-topic")
                .replace("{onlinePlayers}", String.valueOf(getServer().getOnlinePlayers().size()))
                .replace("{maxPlayers}", String.valueOf(getServer().getMaxPlayers())))
                .queue();
    }

    public void broadcastMessage(String message) {
        if (getConfig().getBoolean("relay.discord-to-server.broadcast-to-console")) {
            getServer().broadcastMessage(message);
        } else {
            getServer().getOnlinePlayers().forEach(player -> player.sendMessage(message));
        }
    }

    void sendInternalDiscordEventMessage(String configKey) {
        sendInternalDiscordEventMessage(configKey, Collections.emptyMap());
    }

    void sendInternalDiscordEventMessage(String configKey, Map<String, String> placeholders) {
        boolean enabled = getConfig().getBoolean("relay.events." + configKey + ".enabled");
        if (enabled) {
            String message = getConfig().getString("relay.events." + configKey + ".message");
            boolean showEmbed = getConfig().getBoolean("relay.events." + configKey + ".embed");
            String embedColor = getConfig().getString("relay.events." + configKey + ".embed-color");

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }

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

    /**
     * Send a message to the Discord relay channel.
     *
     * @param message the message to send
     */
    public void sendDiscordMessage(String message) {
        relayChannel.sendMessage(message).queue();
    }

    /**
     * Send an embed to the Discord relay channel.
     *
     * @param embed the embed to send
     */
    public void sendDiscordMessage(MessageEmbed embed) {
        relayChannel.sendMessage(embed).queue();
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
}
