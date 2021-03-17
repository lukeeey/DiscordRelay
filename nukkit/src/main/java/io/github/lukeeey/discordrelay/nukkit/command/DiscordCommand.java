package io.github.lukeeey.discordrelay.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import io.github.lukeeey.discordrelay.nukkit.DiscordRelayPlugin;

public class DiscordCommand extends Command {
    private final DiscordRelayPlugin plugin;

    public DiscordCommand(DiscordRelayPlugin plugin) {
        super("discord", "Display the Discord invite link of this server", "/discord");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(plugin.getConfig().getString("ingame-discord-command-feedback"));
        return true;
    }
}
