package com.minecolonies.luckrankup2.modules.core;

import com.minecolonies.luckrankup2.modules.core.config.AccountConfigData;
import com.minecolonies.luckrankup2.modules.core.config.CoreConfigAdapter;
import com.minecolonies.luckrankup2.modules.core.config.GroupsConfig;
import com.minecolonies.luckrankup2.qsml.modulespec.ConfigurableModule;
import com.minecolonies.luckrankup2.util.PermissionsUtils;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Core module for the Luckrankup2 plugin.
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
    public void luckrankup2Enable()
    {
        perms = new PermissionsUtils(getPlugin(), Sponge.getGame());
        super.luckrankup2Enable();

        final Path accountsPath = this.getPlugin().getConfigDir().resolve("playerstats.conf");
        AccountConfigData data = this.getPlugin().getConfig(accountsPath, AccountConfigData.class,
          HoconConfigurationLoader.builder().setPath(accountsPath).build());
        this.getPlugin().getAllConfigs().put(AccountConfigData.class, data);

        final Path groupsPath = this.getPlugin().getConfigDir().resolve("groups.conf");
        GroupsConfig groups = this.getPlugin().getConfig(groupsPath, GroupsConfig.class,
          HoconConfigurationLoader.builder().setPath(groupsPath).build());
        this.getPlugin().getAllConfigs().put(GroupsConfig.class, groups);

        GroupsConfig config = (GroupsConfig) getPlugin().getAllConfigs().get(GroupsConfig.class);

        List<Subject> disabledGroups = new ArrayList<>();

        for (final Subject subject : perms.getGroups().getAllSubjects())
        {
            final String id = subject.getIdentifier();

            if (!config.groups.containsKey(id))
            {
                config.groups.put(id, new GroupsConfig.GroupConfig());

                config.groups.get(id).enabled = true;
                config.groups.get(id).rank = 0;
            }

            if (!config.groups.get(id).enabled)
            {
                config.groups.get(id).rank = -1;
                disabledGroups.add(subject);
            }
            else if (config.groups.get(id).rank == -1)
            {
                config.groups.get(id).enabled = false;
                disabledGroups.add(subject);
            }
        }

        config.save();

        for (final Subject subject : disabledGroups)
        {
            getPlugin().getLogger().info("Disabled Groups: " + subject.getIdentifier());
        }
    }
}
