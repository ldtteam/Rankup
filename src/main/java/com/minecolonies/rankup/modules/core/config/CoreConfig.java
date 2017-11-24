package com.minecolonies.rankup.modules.core.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

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

    @Setting(value = "prefix-fallback", comment = "This is the prefix that will be put in place if a player has no prefix, leave \"\" to have no prefix fall-back")
    public String prefixFallback = "Default";

    @Setting(value = "rankup-command", comment = "The command the plugin will execute via console to add a rank to a player, valid entries: {player} and {group}")
    public String rankupCommand = "luckperms user {player} parent add {group}";

    @Setting(value = "rankdown-command", comment = "the command the plugin will execute via console to remove a rank from a player, valid entries: {player} and {group}")
    public String rankdownCommand = "luckperms user {player} parent remove {group}";

    @Setting(value = "remove-previous-group", comment = "Whether to remove all other player groups from player after rankUp (Using \"rankdown-command\")")
    public boolean removeOtherGroups = true;

    @Setting(value = "debug-mode", comment = "If true a message will be output to console every time players are updated or checked.")
    public boolean debugMode = false;

    @Setting(value = "track-configs", comment = "This is where all track config names are defined, please be careful here.")
    public List<String> groupConfigs = Arrays.asList("groups.conf", "patreons.conf");

    @Setting(value = "disabled-groups", comment = "List of disabled groups")
    public List<String> disabledGroups = Arrays.asList("admin", "mod", "owner");

    @Setting(comment = "This is weird, but basically Luckperms is screwing with something, anyway, please put your BASE group here (luckperms default is \"default\"")
    public String defaultGroup = "default";

    @Setting(comment = "setting for \"days\" output in time desc")
    public String daysDisplay = "day(s)";

    @Setting(comment = "setting for \"hours\" output in time desc")
    public String hoursDisplay = "hour(s)";

    @Setting(comment = "setting for \"minutes\" output in time desc")
    public String minutesDisplay = "minute(s)";

    @Setting(value = "check-message", comment = "This is the template for the /ru check command, accepted entries are: \n"
                                                  + "{player}        - The Player's name (the one being checked)\n"
                                                  + "{rank}          - The current rank of the player\n"
                                                  + "{prefix}        - The prefix of the player's current group\n"
                                                  + "{timing-time}   - How much time the player has been playing \n"
                                                  + "{timing-next}   - How much time until the player joins the next group \n"
                                                  + "{economy-bal}   - How much money the player has\n"
                                                  + "{economy-next}  - How much money the player needs to join the next group\n"
                                                  + "{joindate}      - The date of when the player joined your server\n"
                                                  + "{lastjoin}      - When the player last joined the server\n"
                                                  + "{track}         - The current track of the player.")
    public List<String> checkMessageTemplate = Arrays.asList(
      "§6---[§2{player}§6]---",
      "§fRank: {rank}",
      "§fTrack: {track}",
      "§fPlay time: {timing-time}",
      "§fTime to next group: {timing-next}",
      "§fJoin date: §a{joindate}",
      "§fLast join: §9{lastjoin}",
      "§6-----------");
}
