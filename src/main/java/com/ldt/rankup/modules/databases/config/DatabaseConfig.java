package com.ldt.rankup.modules.databases.config;

import com.ldt.rankup.internal.configurate.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The database section to the main config file.
 */
@ConfigSerializable
public class DatabaseConfig extends BaseConfig
{
    @Setting(comment = "You may choose between MySQL (Mariadb works too) or H2 (Hikari) databases for Player Stats storage!")
    public String database = "h2";

    @Setting
    public String sqlUsername = "user";

    @SuppressWarnings("squid:S2068")
    @Setting
    public String sqlPassword = "pass";

    @Setting
    public String sqlAddress = "localhost:3306";

    @Setting
    public String sqlDatabase = "rankup";

    @Setting
    public String sqlTablePrefix = "rankup_";
}
