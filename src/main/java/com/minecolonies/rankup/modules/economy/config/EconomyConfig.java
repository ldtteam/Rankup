package com.minecolonies.rankup.modules.economy.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The Economy section to the main config file.
 */
@ConfigSerializable
public class EconomyConfig extends BaseConfig
{

    @Setting(value = "update-interval", comment = "The amount of minutes to pass between economy checks")
    public int updateInterval = 1;
}
