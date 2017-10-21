package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.internal.events.RURankupEvent;
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
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.permission.Subject;

import java.util.List;

public class RankingUtils
{

    public static void timeUp(final Player player, final Rankup plugin)
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

        final String highestGroup = CoreModule.perms.getPlayerHighestRankingGroup(player);
        final String nextGroup = CoreModule.perms.getNextGroup(groupsConfig.groups.get(highestGroup).rank);

        final Integer playerTime = playerData.playerData.get(player.getUniqueId()).timePlayed;


        if (!nextGroup.equals("") && playerTime > groupsConfig.groups.get(nextGroup).timingTime)
        {
            rankUp(player, plugin);
        }
    }

    public static void balanceCheck(final Player player, final Rankup plugin)
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

        final String highestGroup = CoreModule.perms.getPlayerHighestRankingGroup(player);
        final String nextGroup = CoreModule.perms.getNextGroup(groupsConfig.groups.get(highestGroup).rank);

        if (!nextGroup.equals("") && groupsConfig.groups.get(nextGroup).moneyNeeded != 0)
        {
            final UniqueAccount acc = plugin.econ.getOrCreateAccount(player.getUniqueId()).get();
            if (acc.getBalance(plugin.econ.getDefaultCurrency()).intValue() >= groupsConfig.groups.get(nextGroup).moneyNeeded)
            {
                rankUp(player, plugin);
            }
        }
    }

    public static void timeDown(Player player, Rankup plugin)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);

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
                rankDown(player, plugin);
            }
        }
    }

    private static void rankDown(final Player player, final Rankup plugin)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        final String currentGroup = CoreModule.perms.getPlayerHighestRankingGroup(player);
        final String previousGroup = CoreModule.perms.getPreviousGroup(groupsConfig.groups.get(currentGroup).rank);

        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        final String remCmd = coreConfig.rankdownCommand;

        final String finalRemCmd = remCmd.replace("{player}", player.getName())
                                     .replace("{group}", previousGroup);

        plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), finalRemCmd);
    }

    private static void rankUp(final Player player, final Rankup plugin)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        final String currentGroup = CoreModule.perms.getPlayerHighestRankingGroup(player);
        final String nextGroup = CoreModule.perms.getNextGroup(currentGroup);

        if (nextGroup == "")
        {
            return;
        }

        RURankupEvent rankupEvent = new RURankupEvent(player, currentGroup, nextGroup, Sponge.getCauseStackManager().getCurrentCause());
        Sponge.getEventManager().post(rankupEvent);
        if (rankupEvent.isCancelled())
        {
            return;
        }

        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(player);

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

        if (coreConfig.removeOtherGroups)
        {
            for (final String group : playerGroups)
            {
                if (group.equalsIgnoreCase(currentGroup))
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
