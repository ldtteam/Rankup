package com.minecolonies.rankup.modules.core.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import com.minecolonies.rankup.util.Constants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config for plugin's groups used.
 */
@ConfigSerializable
public class GroupsConfig extends BaseConfig
{
    @Setting(value = "group-rank", comment = "This is the rank of a track, the higher the more precedence \n "
                                               + "Basically, if a player has two groups from two tracks the group of higher rank shall be chosen")
    public Integer rank = 0;

    @Setting(comment = "ALL Groups you have\n these are all generated via the plugin on server restart (and possibly on reload in the future)")
    public Map<String, GroupConfig> groups = new HashMap<>();

    @Setting(value = "track-name", comment = "The name of this track (Used for command displays)")
    public String name = "";

    @ConfigSerializable
    public static class GroupConfig
    {

        @Setting(value = "group-enabled", comment = "Whether this group will be part of the RankUp procedures")
        public Boolean enabled = true;

        @Setting(value = "group-rank", comment = "The rank of which the group is, rank one is a starting rank of a player, rank 2 being the next rank they will gain. Set to -1 to ignore")
        public int rank = 0;

        @Setting(value = "group-commands-on-rankup", comment = "When someone ranks up into this group all these commands are run. \n you may use "
                                                                 + Constants.PlayerInfo.PLAYER_NAME + " and " + Constants.PlayerInfo.PLAYER_GROUP + " attributes\n"
                                                                 + "These commands are EXTRA, the rankup commands are in rankup.conf only.")
        public List<String> commands = Arrays.asList("say " + Constants.PlayerInfo.PLAYER_NAME + " joined group " + Constants.PlayerInfo.PLAYER_GROUP + "",
          "give " + Constants.PlayerInfo.PLAYER_NAME + " minecraft:dirt");

        @Setting(value = "timing-time", comment = "The amount of time played required to enter this group")
        public int timingTime = 0;

        @Setting(value = "timing-rankdown", comment = "If this value is true, if a player is within this group and they DO NOT have the require play time, they will be ranked down")
        public boolean timingRankDown = false;

        @Setting(value = "economy-money-needed", comment = "Used by money related modules. The amount of money needed to rankup/buy into this group")
        public int moneyNeeded = 0;
    }
}
