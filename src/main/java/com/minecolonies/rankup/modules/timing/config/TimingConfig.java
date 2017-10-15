package com.minecolonies.rankup.modules.timing.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The timing section to the main config file.
 */
@ConfigSerializable
public class TimingConfig extends BaseConfig
{
    @Setting(value = "update-interval", comment = "The amount of minutes to pass between timing updates")
    public int updateInterval = 1;
}
