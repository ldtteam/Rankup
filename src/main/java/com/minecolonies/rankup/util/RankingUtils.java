package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.modules.timing.TimingModule;
import com.minecolonies.rankup.modules.timing.config.TimingConfig;
import com.minecolonies.rankup.modules.timing.config.TimingConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;

import java.util.List;

public class RankingUtils
{

    public static void rankup(final Player player, final Rankup plugin)
    {
        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(player);

        //Check if player is in disabled group.
        for (final Subject subject : CoreModule.perms.getDisabledGroups())
        {
            if (playerGroups.contains(subject.getIdentifier()))
            {
                return; //IF the player is withing a disabled group, stop here.
            }
        }

        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);
        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();
        final TimingConfig timeConfig = plugin.getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();


        final String highestGroup = CoreModule.perms.getPlayerHighestRankingGroup(player);
        final Integer playerTime = playerData.playerData.get(player.getUniqueId()).timePlayed;

        final String nextGroup = CoreModule.perms.getNextGroup(groupsConfig.groups.get(highestGroup).rank);

        if (!nextGroup.equals("") && playerTime > groupsConfig.groups.get(nextGroup).timingTime)
        {
            final String cmd = coreConfig.rankupCommand;

            final String finalCmd = cmd.replace("{player}", player.getName())
                                      .replace("{group}", nextGroup);

            plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), finalCmd);

            for (final String command : groupsConfig.groups.get(nextGroup).commands)
            {
                final String finalCommand = command.replace("{player}", player.getName())
                                              .replace("{group}", nextGroup);

                plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), finalCommand);
            }

            if (timeConfig.removeOtherGroups)
            {
                for (final String group : playerGroups)
                {
                    if (group.equalsIgnoreCase(highestGroup))
                    {
                        final String remCmd = coreConfig.rankdownCommand;

                        final String finalRemCmd = remCmd.replace("{player}", player.getName())
                                                     .replace("{group}", group);

                        plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), finalRemCmd);
                    }
                }
            }
        }
    }

    public static void rankdown(Player player, Rankup plugin)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);
        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(player);

        //Check if player is in disabled group.
        for (final Subject subject : CoreModule.perms.getDisabledGroups())
        {
            if (playerGroups.contains(subject.getIdentifier()))
            {
                return; //IF the player is withing a disabled group, stop here.
            }
        }

        for (final String group : CoreModule.perms.getPlayerGroupIds(player))
        {
            final Integer time = playerData.playerData.get(player.getUniqueId()).timePlayed;

            if (groupsConfig.groups.get(group).timingRankDown && groupsConfig.groups.get(group).timingTime > time)
            {
                final String remCmd = coreConfig.rankdownCommand;

                final String finalRemCmd = remCmd.replace("{player}", player.getName())
                                             .replace("{group}", group);

                plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), finalRemCmd);
            }
        }
    }
}
