package com.ldt.rankup.modules.timing.config;

import com.ldt.rankup.internal.configurate.BaseConfig;
import com.ldt.rankup.util.Constants;
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

    @Setting(value = "top-message", comment = "Here you may set how you want to have the body of the top command display, accepted entries are same as check-message")
    public List<String> topMessageTemplate = Arrays.asList(
      "§6---[§2" + Constants.PlayerInfo.PLAYER_NAME + "§6]---",
      "§fRank: " + Constants.PlayerInfo.PLAYER_RANK,
      "§f" + Constants.ModuleInfo.TIMING_TIME,
      "§fJoin date: §a" + Constants.PlayerInfo.PLAYER_JOIN,
      "§6-----------");

    @Setting(value = "top-message-head", comment = "This is where you may define how the top of the top message displays")
    public String topMessageHead = "§f------{Top Play Times}------";

    @Setting(value = "top-message-footer", comment = "This is where you may define how the bottom of the top message displays")
    public String topMessageFoot = "§f----------------------------";
}
