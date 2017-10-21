package com.minecolonies.rankup.modules.core;

import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import com.minecolonies.rankup.util.PermissionsUtils;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

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

    public static PermissionsUtils perms;

    @Override
    protected CoreConfigAdapter createConfigAdapter()
    {
        return new CoreConfigAdapter();
    }

    @Override
    public void Rankup2Enable()
    {
        perms = new PermissionsUtils(getPlugin(), Sponge.getGame());
        super.Rankup2Enable();

        final Path accountsPath = this.getPlugin().getConfigDir().resolve("playerstats.conf");
        AccountConfigData data = this.getPlugin().getConfig(accountsPath, AccountConfigData.class,
          HoconConfigurationLoader.builder().setPath(accountsPath).build());
        this.getPlugin().getAllConfigs().put(AccountConfigData.class, data);

        final Path groupsPath = this.getPlugin().getConfigDir().resolve("groups.conf");
        GroupsConfig groups = this.getPlugin().getConfig(groupsPath, GroupsConfig.class,
          HoconConfigurationLoader.builder().setPath(groupsPath).build());
        this.getPlugin().getAllConfigs().put(GroupsConfig.class, groups);

        GroupsConfig groupsConfig = (GroupsConfig) getPlugin().getAllConfigs().get(GroupsConfig.class);

        List<Subject> disabledGroups = new ArrayList<>();

        for (final Subject subject : perms.getGroups().getLoadedSubjects())
        {
            final String id = subject.getIdentifier();

            if (!groupsConfig.groups.containsKey(id))
            {
                groupsConfig.groups.put(id, new GroupsConfig.GroupConfig());

                groupsConfig.groups.get(id).enabled = true;
                groupsConfig.groups.get(id).rank = 0;
            }

            if (!groupsConfig.groups.get(id).enabled)
            {
                groupsConfig.groups.get(id).rank = -1;
                disabledGroups.add(subject);
            }
            else if (groupsConfig.groups.get(id).rank == -1)
            {
                groupsConfig.groups.get(id).enabled = false;
                disabledGroups.add(subject);
            }
        }

        groupsConfig.save();

        for (final Subject subject : disabledGroups)
        {
            getPlugin().getLogger().info("Disabled Groups: " + subject.getIdentifier());
        }
    }
}
