package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.internal.events.RURankupEvent;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.List;

public class RankingUtils
{

    public static void timeUp(final Player player, final Rankup plugin)
    {
        final List<String> playerGroups = plugin.getPerms().getPlayerGroupIds(player);

        //Check if player is in disabled group.
        for (final String subject : plugin.getPerms().getDisabledGroups())
        {
            if (playerGroups.contains(subject))
            {
                //IF the player is withing a disabled group, stop here.
                return;
            }
        }

        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);
        final String nextGroup = plugin.getPerms().getNextGroup(player);

        final Integer playerTime = plugin.getAccUtils().getPlayerTime(player.getUniqueId());

        if (!"".equals(nextGroup) && playerTime > groupsConfig.groups.get(nextGroup).timingTime)
        {
            rankUp(player, plugin);
        }
    }

    public static void balanceCheck(final Player player, final Rankup plugin)
    {
        final List<String> playerGroups = plugin.getPerms().getPlayerGroupIds(player);

        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);

        //Check if player is in disabled group.
        for (final String subject : plugin.getPerms().getDisabledGroups())
        {
            if (playerGroups.contains(subject))
            {
                plugin.getLogger().info("disabled group: " + subject);
                //IF the player is withing a disabled group, stop here.
                return;
            }
        }

        final String nextGroup = plugin.getPerms().getNextGroup(player);

        if (plugin.getEcon() != null && !"".equals(nextGroup) && groupsConfig.groups.get(nextGroup).moneyNeeded != 0)
        {
            final UniqueAccount acc = plugin.getEcon().getOrCreateAccount(player.getUniqueId()).get();
            if (acc.getBalance(plugin.getEcon().getDefaultCurrency()).intValue() >= groupsConfig.groups.get(nextGroup).moneyNeeded)
            {
                rankUp(player, plugin);
            }
        }
    }

    public static void timeDown(Player player, Rankup plugin)
    {
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);
        final List<String> playerGroups = plugin.getPerms().getPlayerGroupIds(player);

        //Check if player is in disabled group.
        for (final String subject : plugin.getPerms().getDisabledGroups())
        {
            if (playerGroups.contains(subject))
            {
                //IF the player is withing a disabled group, stop here.
                return;
            }
        }

        for (final String group : plugin.getPerms().getPlayerGroupIds(player))
        {
            if (groupsConfig.groups.containsKey(group))
            {
                final Integer time = plugin.getAccUtils().getPlayerTime(player.getUniqueId());

                if (groupsConfig.groups.get(group).timingRankDown && groupsConfig.groups.get(group).timingTime > time)
                {
                    rankDown(player, plugin);
                }
            }
        }
    }

    private static void rankDown(final Player player, final Rankup plugin)
    {
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);

        final String currentGroup = plugin.getPerms().getPlayerHighestRankingGroup(player);
        final String previousGroup = plugin.getPerms().getPreviousGroup(player, groupsConfig.groups.get(currentGroup).rank);

        CoreConfig coreConfig = plugin.getConfigUtils().getCoreConfig();

        final String remCmd = coreConfig.rankdownCommand;
        final String finalRemCmd = remCmd.replace(Constants.PlayerInfo.PLAYER_NAME, player.getName())
                                     .replace(Constants.PlayerInfo.PLAYER_GROUP, previousGroup);

        plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), finalRemCmd);
    }

    private static void rankUp(final Player player, final Rankup plugin)
    {
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);

        final String currentGroup = plugin.getPerms().getPlayerHighestRankingGroup(player);
        final String nextGroup = plugin.getPerms().getNextGroup(player);

        if ("".equals(nextGroup))
        {
            return;
        }

        RURankupEvent rankupEvent = new RURankupEvent(player, currentGroup, nextGroup, Cause.of(NamedCause.source(plugin)));
        Sponge.getEventManager().post(rankupEvent);
        if (rankupEvent.isCancelled())
        {
            return;
        }

        CoreConfig coreConfig = plugin.getConfigUtils().getCoreConfig();

        final List<String> playerGroups = plugin.getPerms().getPlayerGroupIds(player);

        final String cmd = coreConfig.rankupCommand;

        final String finalCmd = cmd.replace(Constants.PlayerInfo.PLAYER_NAME, player.getName())
                                  .replace(Constants.PlayerInfo.PLAYER_GROUP, nextGroup);

        plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), finalCmd);

        for (final String command : groupsConfig.groups.get(nextGroup).commands)
        {

            final String finalCommand = command.replace(Constants.PlayerInfo.PLAYER_NAME, player.getName())
                                          .replace(Constants.PlayerInfo.PLAYER_GROUP, nextGroup);

            plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), finalCommand);
        }

        if (coreConfig.removeOtherGroups)
        {
            for (final String group : playerGroups)
            {
                if (group.equalsIgnoreCase(currentGroup))
                {
                    final String remCmd = coreConfig.rankdownCommand;

                    final String finalRemCmd = remCmd.replace(Constants.PlayerInfo.PLAYER_NAME, player.getName())
                                                 .replace(Constants.PlayerInfo.PLAYER_GROUP, group);

                    plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), finalRemCmd);
                }
            }
        }
    }
}
