package dev.lukeeey.discordrelay.util;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {
    private final String name;
    private final UUID uniqueId;
    private final float health;
    private final int food;
    private final String lastSeen;
}
