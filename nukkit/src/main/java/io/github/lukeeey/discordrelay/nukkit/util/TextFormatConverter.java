package io.github.lukeeey.discordrelay.nukkit.util;

import cn.nukkit.utils.TextFormat;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// This class (except a few minor edits) can be accredited to Vexentric:
// https://gist.github.com/Vexentric/1a2a565377a0970951172a39283dad53
public class TextFormatConverter {
    private static Map<TextFormat, ColorSet<Integer, Integer, Integer>> colorMap = new HashMap<>();

    static {
        colorMap.put(TextFormat.BLACK, new ColorSet<>(0, 0, 0));
        colorMap.put(TextFormat.DARK_BLUE, new ColorSet<>(0, 0, 170));
        colorMap.put(TextFormat.DARK_GREEN, new ColorSet<>(0, 170, 0));
        colorMap.put(TextFormat.DARK_AQUA, new ColorSet<>(0, 170, 170));
        colorMap.put(TextFormat.DARK_RED, new ColorSet<>(170, 0, 0));
        colorMap.put(TextFormat.DARK_PURPLE, new ColorSet<>(170, 0, 170));
        colorMap.put(TextFormat.GOLD, new ColorSet<>(255, 170, 0));
        colorMap.put(TextFormat.GRAY, new ColorSet<>(170, 170, 170));
        colorMap.put(TextFormat.DARK_GRAY, new ColorSet<>(85, 85, 85));
        colorMap.put(TextFormat.BLUE, new ColorSet<>(85, 85, 255));
        colorMap.put(TextFormat.GREEN, new ColorSet<>(85, 255, 85));
        colorMap.put(TextFormat.AQUA, new ColorSet<>(85, 255, 255));
        colorMap.put(TextFormat.RED, new ColorSet<>(255, 85, 85));
        colorMap.put(TextFormat.LIGHT_PURPLE, new ColorSet<>(255, 85, 255));
        colorMap.put(TextFormat.YELLOW, new ColorSet<>(255, 255, 85));
        colorMap.put(TextFormat.WHITE, new ColorSet<>(255, 255, 255));
    }

    @Value
    private static class ColorSet<R, G, B> {
        R red;
        G green;
        B blue;
    }

    public static TextFormat fromRGB(int r, int g, int b) {
        TreeMap<Integer, TextFormat> closest = new TreeMap<>();
        colorMap.forEach((color, set) -> {
            int red = Math.abs(r - set.getRed());
            int green = Math.abs(g - set.getGreen());
            int blue = Math.abs(b - set.getBlue());
            closest.put(red + green + blue, color);
        });
        return closest.firstEntry().getValue();
    }
}
