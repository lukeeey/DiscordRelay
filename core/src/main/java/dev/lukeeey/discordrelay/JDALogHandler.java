package dev.lukeeey.discordrelay;

import lombok.RequiredArgsConstructor;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@RequiredArgsConstructor
public class JDALogHandler extends Handler {
    private final DiscordRelayPlatform platform;

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;

        Level level = record.getLevel();
        String message = record.getMessage();

        if (level == Level.SEVERE) {
            platform.getAdapter().logError("[JDA] " + message);
        } else if (level == Level.WARNING) {
            platform.getAdapter().logWarning("[JDA] " + message);
        } else if (level == Level.INFO) {
            platform.getAdapter().logInfo("[JDA] " + message);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
