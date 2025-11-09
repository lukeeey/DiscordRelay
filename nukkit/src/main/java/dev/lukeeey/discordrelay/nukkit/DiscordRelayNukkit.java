package dev.lukeeey.discordrelay.nukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import lombok.Getter;

import javax.security.auth.login.LoginException;

public class DiscordRelayNukkit extends PluginBase {
    @Getter
    private static DiscordRelayNukkit instance;

    @Getter
    private DiscordRelayPlatform platform;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        platform = new DiscordRelayPlatform(new DiscordRelayNukkitAdapter(this));

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        try {
            platform.initJDA();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        platform.registerCommands();
        platform.sendInternalDiscordEventMessage("server-start");

        getLogger().info(TextFormat.BLUE + "DiscordRelay by lukeeey has been enabled!");
    }

    @Override
    public void onDisable() {
        platform.sendInternalDiscordEventMessage("server-stop");

        if (platform.getJda() != null) {
            platform.getJda().shutdown();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discord") && getConfig().getBoolean("ingame-discord-command-enabled")) {
            sender.sendMessage(TextFormat.colorize('&', getConfig().getString("ingame-discord-command-response")));
        }
        if (command.getName().equalsIgnoreCase("discordrelay")) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender.hasPermission("drelay.reload")) {
                        reloadConfig();
                        sender.sendMessage(TextFormat.GREEN + "DiscordRelay config has been reloaded!");
                    }
                    break;
                case "restart":
                    if (sender.hasPermission("drelay.restart")) {
                        sender.sendMessage(TextFormat.YELLOW + "Shutting down the bot...");
                        platform.getJda().shutdownNow();

                        sender.sendMessage(TextFormat.YELLOW + "Starting the bot...");
                        try {
                            platform.initJDA();
                            sender.sendMessage(TextFormat.GREEN + "The bot has successfully been restarted!");
                        } catch (LoginException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
