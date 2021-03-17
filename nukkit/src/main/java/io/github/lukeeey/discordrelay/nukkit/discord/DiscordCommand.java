package io.github.lukeeey.discordrelay.nukkit.discord;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Represents a command that can be executed in Discord.
 */
@Getter
public abstract class DiscordCommand {
    private final String name;
    private final String description;

    public DiscordCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void execute(Member sender, TextChannel channel, String message);
}
