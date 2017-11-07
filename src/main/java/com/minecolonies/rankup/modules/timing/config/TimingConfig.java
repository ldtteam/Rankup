package com.minecolonies.rankup.modules.timing.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

/**
 * The timing section to the main config file.
 */
@ConfigSerializable
public class TimingConfig extends BaseConfig
{
    @Setting(value = "update-interval", comment = "The amount of minutes to pass between timing updates")
    public int updateInterval = 1;

    @Setting(value = "top-message", comment = "Here you may set how you want to have the body of the top command display, accepted entries are:\n"
                                                + "{player}        - The Player's name (the one being checked)\n"
                                                + "{rank}          - The current rank of the player\n"
                                                + "{prefix}        - The prefix of the player's current group\n"
                                                + "{timing-time}   - How much time the player has been playing \n"
                                                + "{timing-next}   - How much time until the player joins the next group \n"
                                                + "{economy-bal}   - How much money the player has\n"
                                                + "{economy-next}  - How much money the player needs to join the next group\n"
                                                + "{joindate}      - The date of when the player joined your server\n"
                                                + "{lastjoin}      - When the player last joined the server")
    public List<String> topMessageTemplate = Arrays.asList(
      "§6---[§2{player}§6]---",
      "§fRank: {rank}",
      "§f{timing-time}",
      "§fJoin date: §a{joindate}",
      "§6-----------");

    @Setting(value = "top-message-head", comment = "This is where you may define how the top of the top message displays")
    public String topMessageHead = "§f------{Top Play Times}------";

    @Setting(value = "top-message-footer", comment = "This is where you may define how the bottom of the top message displays")
    public String topMessageFoot = "§f----------------------------";
}
