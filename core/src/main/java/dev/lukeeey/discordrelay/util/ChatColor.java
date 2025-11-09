package dev.lukeeey.discordrelay.util;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public enum ChatColor {
    BLACK('0', "Black"),
    DARK_BLUE('1', "Dark Blue"),
    DARK_GREEN('2', "Dark Green"),
    DARK_AQUA('3', "Dark Aqua"),
    DARK_RED('4', "Dark Red"),
    DARK_PURPLE('5', "Dark Purple"),
    GOLD('6', "Gold"),
    GRAY('7', "Gray"),
    DARK_GRAY('8', "Dark Gray"),
    BLUE('9', "Blue"),
    GREEN('a', "Green"),
    AQUA('b', "Aqua"),
    RED('c', "Red"),
    LIGHT_PURPLE('d', "Light Purple"),
    YELLOW('e', "Yellow"),
    WHITE('f', "White"),

    // formatting (still part of chat codes)
    OBFUSCATED('k', "Obfuscated"),
    BOLD('l', "Bold"),
    STRIKETHROUGH('m', "Strikethrough"),
    UNDERLINE('n', "Underline"),
    ITALIC('o', "Italic"),
    RESET('r', "Reset");

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-ORX]");

    private final char code;
    private final String name;

    ChatColor(char code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String clean(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public String toString() {
        return "§" + code;
    }
}
