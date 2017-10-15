package com.minecolonies.rankup.modules.magibridge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The Magibridge section to the main config file.
 */
@ConfigSerializable
public class MagibridgeConfig
{

    @Setting(value = "Rankup-Message", comment = "Message to discord when a player ranks up! \n valid entries: {player} and {group}")
    public String rankupMessage = "Player {player} has ranked up into the group {group}!";
}
