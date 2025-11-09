![image](https://user-images.githubusercontent.com/32024335/111518790-53ea6080-874e-11eb-9490-bfc2aeb7713c.png)

![DiscordRelay](https://github.com/lukeeey/DiscordRelay/workflows/DiscordRelay/badge.svg)
[![Discord](https://img.shields.io/discord/803794932820082739.svg?color=%237289da&label=Discord)](https://discord.gg/wXFFSkmANS)   
A plugin for relaying chat between Discord and Minecraft.

## Features
* Two-way chat between Discord and Minecraft
* Display messages in Discord (optionally in embeds) for common events like player joins or deaths
* Role ping protection from Minecraft
* Simple Developer API for creating new Discord commands
* Built-in Discord commands:
  * `!playerlist`
  * `!serverinfo`
  * `!playerinfo <player name>`
* Built-in `/discord` in-game command so players can see info about your Discord server  
* Automatic updating of the relay channel topic in Discord!  
* Change the bot's status type between `Playing`, `Streaming` and `Listening`
* Restart the bot with `/drelay restart` without restarting the server
* Reload the config with `/drelay reload`
...and more!

### Bukkit Only Features
Some features are only available for Java Edition servers.

* PlaceholderAPI support
* Additional built-in Discord commands:
  * `!cape <player name>`
  * `!skin <player name>`
  * `!avatar <player name>`
  
And best of all, **everything is configurable!**

## Permissions
|Permission|Default|Description|
|----------|-------|-----------|
|`drelay.reload`|`op`|Allow using the `/drelay reload` command to reload the config|
|`drelay.restart`|`op`|Allow using the `/drelay restart` command to restart the bot|
|`drelay.allowrolepings`|`op`|Allow the player to ping users/roles by typing e.g. `<@382274626269307392>`|
|`drelay.sendtodiscord`|`everyone`|The players chat messages will be sent to Discord|
|`drelay.receivefromdiscord`|`everyone`|The player will see messages sent from Discord|

## Plugin Setup
1. Create a new application [here](https://discord.com/developers/applications)
2. Navigate to the "Bot" tab on the left side and press "Add Bot"
3. Press "Click to Reveal" and copy the token to your config.yml file
4. Go to the following page and click "Authorize"
```
https://discord.com/oauth2/authorize?client_id=YOUR-CLIENT-ID-HERE&permissions=11264&scope=bot
```
(Make sure to replace YOUR-CLIENT-ID-HERE with your "Application ID" which can be found on the General Information tab)

5. Ensure "Message Content Intent" is ticked
![image](https://i.imgur.com/H2Slkdy.png)
5. Enable developer mode in Discord by going to settings then the "Advanced" tab
6. Right-click the channel you would like to be used for relaying chat and select "Copy ID"
7. Paste the copied ID into your config.yml file
8. Make sure the bot has permissions to send and receive messages

It may look a bit daunting but it's actually quite easy!

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
##### Bukkit (Java)
```java
DiscordRelayBukkit discordRelay = getServer().getPluginManager().getPlugin("DiscordRelay");
discordRelay.getAdapter().registerDiscordCommand(new HelloWorldCommand());
```

##### Nukkit (Bedrock)
```java
DiscordRelayNukkit discordRelay = getServer().getPluginManager().getPlugin("DiscordRelay");
discordRelay.getAdapter().registerDiscordCommand(new HelloWorldCommand());
```
When typing `!helloworld` the bot will send a message saying `Hello, world!`

