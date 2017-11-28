package com.minecolonies.rankup.modules.magibridge.config;

import com.minecolonies.rankup.util.Constants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The Magibridge section to the main config file.
 */
@ConfigSerializable
public class MagibridgeConfig
{

    @Setting(value = "rankup-Message", comment = "Message to discord when a player ranks up! \n valid entries: " + Constants.PlayerInfo.PLAYER_NAME + ", "
                                                   + Constants.ModuleInfo.MAGIBRIDGE_NEXT + " and " + Constants.ModuleInfo.MAGIBRIDGE_CURR + "")
    public String rankupMessage =
      "Player " + Constants.PlayerInfo.PLAYER_NAME + " has left the " + Constants.ModuleInfo.MAGIBRIDGE_CURR + " group and joined the " + Constants.ModuleInfo.MAGIBRIDGE_NEXT
        + " group! :smile:";

    @Setting(value = "send-in-staff", comment = "Set to true if you want the rankup message sent in the MC-Staff channel instead!"
                                                  + "\n (Only possible if Magibridge is set to use Nucleus)")
    public boolean sendInStaff = false;
}
