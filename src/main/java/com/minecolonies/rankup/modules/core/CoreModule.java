package com.minecolonies.rankup.modules.core;

import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.service.permission.Subject;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Core module for the Rankup plugin.
 */
@ModuleData(id = CoreModule.ID, name = "Core", isRequired = true)
public class CoreModule extends ConfigurableModule<CoreConfigAdapter>
{

    public static final String ID = "core";

    @Override
    protected CoreConfigAdapter createConfigAdapter()
    {
        return new CoreConfigAdapter();
    }

    @Override
    public void Rankup2Enable()
    {
        super.Rankup2Enable();

        final Path accountsPath = this.getPlugin().getConfigDir().resolve("playerstats.conf");
        AccountConfigData data = this.getPlugin().getConfig(accountsPath, AccountConfigData.class,
          HoconConfigurationLoader.builder().setPath(accountsPath).build());
        this.getPlugin().getAllConfigs().put(AccountConfigData.class, data);

        CoreConfig config = getPlugin().getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        final File dir = new File(this.getPlugin().getConfigDir().resolve("group-configs").toString());

        if (!dir.exists())
        {
            dir.mkdir();
        }

        for (final String name : config.groupConfigs)
        {
            final Path confPath = this.getPlugin().getConfigDir().resolve("group-configs").resolve(name);
            GroupsConfig groups = this.getPlugin().getConfig(confPath, GroupsConfig.class,
              HoconConfigurationLoader.builder().setPath(confPath).build());
            this.getPlugin().getGroupConfigs().add(groups);
        }

        for (final GroupsConfig groupConfig : getPlugin().getGroupConfigs())
        {
            if (groupConfig.groups.isEmpty())
            {
                initGroupConfig(groupConfig);
            }
            checkConfig(groupConfig);
        }
    }

    public void initGroupConfig(final GroupsConfig groupsConfig)
    {
        for (final Subject subject : getPlugin().perms.getGroups().getLoadedSubjects())
        {
            final String id = subject.getIdentifier();

            if (!groupsConfig.groups.containsKey(id))
            {
                groupsConfig.groups.put(id, new GroupsConfig.GroupConfig());

                groupsConfig.groups.get(id).enabled = true;
                groupsConfig.groups.get(id).rank = 0;
            }
        }

        groupsConfig.save();
    }

    public void checkConfig(final GroupsConfig groupsConfig)
    {
        final List<String> groupsToRemove = new ArrayList<>();

        for (final String group : groupsConfig.groups.keySet())
        {
            getPlugin().getLogger().info("group check: " + group);
            if (!groupsConfig.groups.get(group).enabled)
            {
                getPlugin().getLogger().info("group remove: " + group);
                groupsToRemove.add(group);
            }
        }

        for (final String group : groupsToRemove)
        {
            groupsConfig.groups.remove(group);
        }

        groupsConfig.save();
    }
}
