# DiscordRelay
![DiscordRelay](https://github.com/lukeeey/DiscordRelay/workflows/DiscordRelay/badge.svg)  
A plugin for relaying chat between Discord and Minecraft.

## Features
* Two-way chat between Discord and Minecraft
* Display messages in Discord (optionally in embeds) for common events like player joins or deaths
* Role ping protection from Minecraft
* Simple Developer API for creating new Discord commands
* Built-in `!playerlist` Discord command for seeing who's online
* Built-in `/discord` in-game command so players can see info about your Discord server  
* Automatic updating of the relay channel topic in Discord!  
...and more!
  
And best of all, **everything is configurable!**

## TODO
* Make use of PlaceholderAPI  
* Allow the execution of commands from Discord  
* Add more default Discord commands

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

