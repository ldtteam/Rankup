package com.minecolonies.rankup.modules.core.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The core section to the main config file.
 */
@ConfigSerializable
public class CoreConfig extends BaseConfig
{
    @Setting(value = "welcome-message", comment = "The message to display for a player in chat when they login to the server")
    public String welcomeMessage = "Welcome back {player}, your current rank is {prefix}!";

    @Setting(value = "first-time-welcome-message", comment = "The message to display for a player in chat when they login to the server for the first time")
    public String firstWelcomeMessage = "Welcome to our server {player}!";

    @Setting(value = "date-format", comment = "Date format to save data info of players.")
    public String dateFormat = "dd/MM/yyyy";

    @Setting(value = "prefix-fallback", comment = "This is the prefix that will be put in place if a player has no prefix, leave \"\" to have no prefix fall-back")
    public String prefixFallback = "Default";

    @Setting(value = "rankup-command", comment = "The command the plugin will execute via console to add a rank to a player, valid entries: {player} and {group}")
    public String rankupCommand = "luckperms user {player} parent add {group}";

    @Setting(value = "rankdown-command", comment = "the command the plugin will execute via console to remove a rank from a player, valid entries: {player} and {group}")
    public String rankdownCommand = "luckperms user {player} parent remove {group}";
}
