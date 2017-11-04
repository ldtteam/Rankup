package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.modules.databases.DatabaseModule;
import com.minecolonies.rankup.modules.databases.config.DatabaseConfig;
import com.minecolonies.rankup.modules.databases.config.DatabaseConfigAdapter;
import com.minecolonies.rankup.modules.economy.EconomyModule;
import com.minecolonies.rankup.modules.economy.config.EconomyConfig;
import com.minecolonies.rankup.modules.economy.config.EconomyConfigAdapter;
import com.minecolonies.rankup.modules.magibridge.MagibridgeModule;
import com.minecolonies.rankup.modules.magibridge.config.MagibridgeConfig;
import com.minecolonies.rankup.modules.magibridge.config.MagibridgeConfigAdapter;
import com.minecolonies.rankup.modules.timing.TimingModule;
import com.minecolonies.rankup.modules.timing.config.TimingConfig;
import com.minecolonies.rankup.modules.timing.config.TimingConfigAdapter;

/**
 * Tonnes of config Utils.. holy cow.
 */
public class ConfigUtils
{

    Rankup plugin;

    public ConfigUtils(Rankup pl)
    {
        this.plugin = pl;
    }

    public CoreConfig getCoreConfig()
    {
        if (plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).isPresent())
        {
            return plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();
        }
        return null;
    }

    public TimingConfig getTimingConfig()
    {
        if (plugin.getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).isPresent())
        {
            return plugin.getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();
        }
        return null;
    }

    public EconomyConfig getEconomyConfig()
    {
        if (plugin.getConfigAdapter(EconomyModule.ID, EconomyConfigAdapter.class).isPresent())
        {
            return plugin.getConfigAdapter(EconomyModule.ID, EconomyConfigAdapter.class).get().getNodeOrDefault();
        }
        return null;
    }

    public MagibridgeConfig getMagibridgeConfig()
    {
        if (plugin.getConfigAdapter(MagibridgeModule.ID, MagibridgeConfigAdapter.class).isPresent())
        {
            return plugin.getConfigAdapter(MagibridgeModule.ID, MagibridgeConfigAdapter.class).get().getNodeOrDefault();
        }
        return null;
    }

    public DatabaseConfig getDatabasesConfig()
    {
        if (plugin.getConfigAdapter(DatabaseModule.ID, DatabaseConfigAdapter.class).isPresent())
        {
            return plugin.getConfigAdapter(DatabaseModule.ID, DatabaseConfigAdapter.class).get().getNodeOrDefault();
        }
        return null;
    }

    public AccountConfigData getAccountConfig()
    {
        return (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);
    }

    public GroupsConfig getGroupsConfig()
    {
        return (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);
    }
}
