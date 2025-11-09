package dev.lukeeey.discordrelay.bukkit;

import dev.lukeeey.discordrelay.DiscordRelayPlatform;
import dev.lukeeey.discordrelay.bukkit.placeholders.DiscordPlaceholderHook;
import dev.lukeeey.discordrelay.bukkit.discord.AvatarCommand;
import dev.lukeeey.discordrelay.bukkit.discord.CapeCommand;
import dev.lukeeey.discordrelay.bukkit.discord.SkinCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class DiscordRelayBukkit extends JavaPlugin implements CommandExecutor {
    @Getter
    private static DiscordRelayBukkit instance;

    @Getter
    private DiscordRelayPlatform platform;

    @Getter
    private boolean placeholderApiEnabled;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        platform = new DiscordRelayPlatform(new DiscordRelayBukkitAdapter(this));

        if (getConfig().getBoolean("ingame-discord-command-enabled")) {
            getCommand("discord").setExecutor(this);
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI detected, delaying to allow expansions to register before continuing");
            new DiscordPlaceholderHook().register();
            placeholderApiEnabled = true;
            getServer().getScheduler().runTaskLater(this, this::continueInit, 20);
        } else {
            continueInit();
        }
    }

    private void continueInit() {
        try {
            platform.initJDA();
        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        platform.registerCommands();

        // Register Bukkit exclusive commands
        platform.registerDiscordCommand(new SkinCommand(platform));
        platform.registerDiscordCommand(new CapeCommand(platform));
        platform.registerDiscordCommand(new AvatarCommand(platform));

        platform.sendInternalDiscordEventMessage("server-start");

        getLogger().info(ChatColor.BLUE + "DiscordRelay by lukeeey has been enabled!");
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
        if (command.getName().equalsIgnoreCase("discord")) {
            Player player = sender instanceof Player ? (Player) sender : null;

            sender.sendMessage(platform.getAdapter().placeholderApiSupport(
                    ChatColor.translateAlternateColorCodes('&',
                            getConfig().getString("ingame-discord-command-response")), player));
        }
        if (command.getName().equalsIgnoreCase("discordrelay")) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender.hasPermission("drelay.reload")) {
                        reloadConfig();
                        sender.sendMessage(ChatColor.GREEN + "DiscordRelay config has been reloaded!");
                    }
                    break;
                case "restart":
                    if (sender.hasPermission("drelay.restart")) {
                        sender.sendMessage(ChatColor.YELLOW + "Shutting down the bot...");
                        platform.getJda().shutdownNow();

                        sender.sendMessage(ChatColor.YELLOW + "Starting the bot...");
                        try {
                            platform.initJDA();
                            sender.sendMessage(ChatColor.GREEN + "The bot has successfully been restarted!");
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
