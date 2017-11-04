package com.minecolonies.rankup.modules.databases.config;

import com.minecolonies.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The database section to the main config file.
 */
@ConfigSerializable
public class DatabaseConfig extends BaseConfig
{
    @Setting
    public String database = "h2";
}
