package com.minecolonies.rankup.modules.magibridge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The Magibridge section to the main config file.
 */
@ConfigSerializable
public class MagibridgeConfig
{

    @Setting(value = "rankup-Message", comment = "Message to discord when a player ranks up! \n valid entries: {player}, {next_group} and {current_group}")
    public String rankupMessage = "Player {player} has left the {current_group} group and joined the {next_group} group! :smile:";

    @Setting(value = "send-in-staff", comment = "Set to true if you want the rankup message sent in the MC-Staff channel instead! \n (Only possible if Magibridge is set to use Nucleus)")
    public boolean sendInStaff = false;
}
