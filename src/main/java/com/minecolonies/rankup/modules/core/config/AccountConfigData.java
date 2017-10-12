package com.minecolonies.luckrankup2.modules.core.config;

import com.minecolonies.luckrankup2.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ConfigSerializable
public class AccountConfigData extends BaseConfig
{

    @Setting
    public Map<UUID, PlayerConfig> playerData = new HashMap<>();

    @ConfigSerializable
    public static class PlayerConfig
    {
        @Setting(value = "JoinDate")
        public String joinDate = "";

        @Setting(value = "LastVisit")
        public String lastVisit = "";

        @Setting(value = "PlayerName")
        public String playerName = "";

        @Setting(value = "TimePlayed")
        public int timePlayed = 0;
    }
}
