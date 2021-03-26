package io.github.lukeeey.discordrelay.bukkit.placeholders;

import io.github.lukeeey.discordrelay.bukkit.DiscordRelayPlugin;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordPlaceholderHook extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String request = _onPlaceholderRequest(player, identifier);
        if (request == null) {
            return "";
        }
        return request;
    }

    private String _onPlaceholderRequest(Player player, String identifier) {
        DiscordRelayPlugin plugin =  DiscordRelayPlugin.getInstance();
        TextChannel relayChannel = plugin.getRelayChannel();

        switch (identifier) {
            case "guild_member_count":
                return String.valueOf(relayChannel.getGuild().getMemberCount());
            case "guild_id":
                return relayChannel.getGuild().getId();
            case "guild_name":
                return relayChannel.getGuild().getName();
            case "guild_icon_id":
                return relayChannel.getGuild().getIconId();
                // Features
            case "guild_icon_url":
                return relayChannel.getGuild().getIconUrl();
            case "guild_splash_id":
                return relayChannel.getGuild().getSplashId();
            case "guild_splash_url":
                return relayChannel.getGuild().getSplashUrl();
            case "guild_vanity_code":
                return relayChannel.getGuild().getVanityCode();
            case "guild_vanity_url":
                return relayChannel.getGuild().getVanityUrl();
            case "guild_description":
                return relayChannel.getGuild().getDescription();
            case "guild_banner_id":
                return relayChannel.getGuild().getBannerId();
            case "guild_banner_url":
                return relayChannel.getGuild().getBannerUrl();
                // boost tiers
            case "guild_boost_count":
                return String.valueOf(relayChannel.getGuild().getBoostCount());
            case "guild_max_bitrate":
                return String.valueOf(relayChannel.getGuild().getMaxBitrate());
            case "guild_max_emotes":
                return String.valueOf(relayChannel.getGuild().getMaxEmotes());
            case "guild_max_members":
                return String.valueOf(relayChannel.getGuild().getMaxMembers());

            case "guild_afk_channel_id":
                return relayChannel.getGuild().getAfkChannel().getId();
            case "guild_afk_channel_name":
                return relayChannel.getGuild().getAfkChannel().getName();
            case "guild_system_channel_id":
                return relayChannel.getGuild().getSystemChannel().getId();
            case "guild_system_channel_name":
                return relayChannel.getGuild().getSystemChannel().getName();

            case "guild_owner_id":
                return relayChannel.getGuild().getOwner().getId();
            case "guild_owner_nickname":
                return relayChannel.getGuild().getOwner().getNickname();
            case "guild_owner_effective_name":
                return relayChannel.getGuild().getOwner().getEffectiveName();
            case "guild_owner_discriminator":
                return relayChannel.getGuild().getOwner().getUser().getDiscriminator();

            case "guild_region":
                return relayChannel.getGuild().getRegion().getName();
        }
        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "discord";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lukeeey";
    }

    @Override
    public @NotNull String getVersion() {
        String version = DiscordRelayPlugin.class.getPackage().getImplementationVersion();
        return version != null ? version : "unknown";
    }

    private String getBoolean(boolean value) {
        return value ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }
}
