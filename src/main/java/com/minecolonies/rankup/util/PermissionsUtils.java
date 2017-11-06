package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;

import java.util.*;

public class PermissionsUtils
{
    private PermissionService permissionService;
    private Rankup            plugin;

    public PermissionsUtils(Rankup pl, Game game)
    {
        this.plugin = pl;
        this.permissionService = game.getServiceManager().getRegistration(PermissionService.class).get().getProvider();
    }

    public SubjectCollection getGroups()
    {
        return permissionService.getGroupSubjects();
    }

    public List<Subject> getDisabledGroups()
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        List<Subject> disabledGroups = new ArrayList<>();

        for (final Subject subject : getGroups().getAllSubjects())
        {
            final String id = subject.getIdentifier();

            if (groupsConfig.groups.containsKey(id) && !groupsConfig.groups.get(id).enabled)
            {
                disabledGroups.add(subject);
            }
        }

        return disabledGroups;
    }

    public String getPlayerHighestRankingGroup(Player player)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(player))
        {

            final int rank = groupsConfig.groups.get(group).rank;

            if (groupsConfig.groups.containsKey(group) && rank > currentRank)
            {
                currentGroup = group;
                currentRank = rank;
            }
        }

        return currentGroup;
    }

    public String getPlayerHighestRankingGroup(User user)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(user))
        {

            final int rank = groupsConfig.groups.get(group).rank;

            if (groupsConfig.groups.containsKey(group) && rank > currentRank)
            {
                currentGroup = group;
                currentRank = rank;
            }
        }

        return currentGroup;
    }

    public Integer timeToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        final String currentGroup = getPlayerHighestRankingGroup(user);
        final String nextGroup = getNextGroup(groupsConfig.groups.get(currentGroup).rank);

        if (nextGroup.equals(""))
        {
            return -1;
        }

        return groupsConfig.groups.get(nextGroup).timingTime - plugin.accUtils.getPlayerTime(user.getUniqueId());
    }

    public Integer balanceToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        int userMoney;
        if (plugin.econ != null && plugin.econ.getOrCreateAccount(user.getUniqueId()).isPresent())
        {
            UniqueAccount acc = plugin.econ.getOrCreateAccount(user.getUniqueId()).get();
            userMoney = acc.getBalance(plugin.econ.getDefaultCurrency()).intValue();
        }
        else
        {
            userMoney = 0;
        }

        if (getNextGroup(getPlayerHighestRankingGroup(user)).equals(""))
        {
            return 0;
        }
        return groupsConfig.groups.get(getNextGroup(getPlayerHighestRankingGroup(user))).moneyNeeded - userMoney;
    }

    public String getNextGroup(final String currentGroup)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = groupsConfig.groups.get(currentGroup).rank + 1;

        groupsConfig.groups.forEach((name, conf) ->
                                      ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }

        return "";
    }

    public String getNextGroup(final int currentRank)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = currentRank + 1;

        groupsConfig.groups.forEach((name, conf) ->
                                      ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }

        return "";
    }

    public String getPreviousGroup(final int currentRank)
    {
        final GroupsConfig groupsConfig = plugin.configUtils.getGroupsConfig();

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = currentRank - 1;

        groupsConfig.groups.forEach((name, conf) ->
                                      ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }

        return "";
    }

    public List<Subject> getPlayerGroups(Player player)
    {
        return player.getParents();
    }

    public List<Subject> getPlayerGroups(User user)
    {
        return user.getParents();
    }

    public List<String> getPlayerGroupIds(Player player)
    {
        final List<String> ids = new ArrayList<>();

        for (final Subject subject : getPlayerGroups(player))
        {
            ids.add(subject.getIdentifier());
        }

        return ids;
    }

    public List<String> getPlayerGroupIds(User user)
    {
        final List<String> ids = new ArrayList<>();

        for (final Subject subject : getPlayerGroups(user))
        {
            ids.add(subject.getIdentifier());
        }

        return ids;
    }

    public String getPlayerGroupWithMostParents(Player player)
    {
        Iterator<Subject> groups_iterator = getPlayerGroups(player).iterator();

        if (player == null)
        {
            return null;
        }

        int i = -1;
        Subject currentGroup = null;

        while (groups_iterator.hasNext())
        {
            Subject group = groups_iterator.next();
            int size = group.getParents().size();

            if (size > i)
            {
                i = size;
                currentGroup = group;
            }
        }

        if (currentGroup != null)
        {
            return currentGroup.getIdentifier();
        }
        else
        {
            return null;
        }
    }
}
