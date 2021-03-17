# DiscordRelay
![DiscordRelay](https://github.com/lukeeey/DiscordRelay/workflows/DiscordRelay/badge.svg)  
A plugin for relaying chat between Discord and Minecraft.

## Default commands
### In-game
* `/discord` - Display the Discord server link, response is configurable

### Discord
The following are default commands that can be disabled, however developers can add their own commands. See section below.
* `playerlist` - Display a list of online players on the server

## For Developers
You can register your own Discord command and also send your own messages to the Discord relay channel.

**Registering a command**
```java
public class HelloWorldCommand extends DiscordCommand {

    public HelloWorldCommand() {
        super("helloworld", "Says \"Hello, world!\"");
    }

    @Override
    public void execute(Member sender, TextChannel channel, String message) {
        channel.sendMessage("Hello, world!").queue();
    }
}
```
In your `onEnable` simply add something like
```java
DiscordRelayPlugin discordRelay = getServer().getPluginManager().getPlugin("DiscordRelay");
discordRelay.registerDiscordCommand(new HelloWorldCommand());
```
When typing `!helloworld` the bot will send a message saying `Hello, world!`

