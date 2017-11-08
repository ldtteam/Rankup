package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
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

    public GroupsConfig getGroupConfig(final Player player)
    {
        Integer currentRank = 0;
        GroupsConfig currentConf = null;

        for (final GroupsConfig groupConf : plugin.getGroupConfigs())
        {
            for (final String group : groupConf.groups.keySet())
            {
                if (getPlayerGroupIds(player).contains(group) && currentRank <= groupConf.rank)
                {
                    currentRank = groupConf.rank;
                    currentConf = groupConf;
                }
            }
        }
        if (currentConf == null)
        {
            plugin.getLogger().info("Well crap, there's apparently an issue with getting this players group config! \n"
                                      + "Their groups are: " + getPlayerGroupIds(player));
        }
        return currentConf;
    }

    public GroupsConfig getGroupConfig(final User player)
    {
        Integer currentRank = 0;
        GroupsConfig currentConf = null;

        for (final GroupsConfig groupConf : plugin.getGroupConfigs())
        {
            for (final String group : groupConf.groups.keySet())
            {
                if (getPlayerGroupIds(player).contains(group) && currentRank <= groupConf.rank)
                {
                    currentRank = groupConf.rank;
                    currentConf = groupConf;
                }
            }
        }
        if (currentConf == null)
        {
            plugin.getLogger().info("Well crap, there's apparently an issue with getting this players group config! \n"
                                      + "Their groups are: " + getPlayerGroupIds(player));
        }
        return currentConf;
    }

    public SubjectCollection getGroups()
    {
        return permissionService.getGroupSubjects();
    }

    public List<String> getDisabledGroups()
    {
        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        return coreConfig.disabledGroups;
    }

    public String getPlayerHighestRankingGroup(final Player player)
    {
        final GroupsConfig config = getGroupConfig(player);

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(player))
        {
            if (config.groups.containsKey(group))
            {
                final int rank = config.groups.get(group).rank;

                if (config.groups.containsKey(group) && rank > currentRank)
                {
                    currentGroup = group;
                    currentRank = rank;
                }
            }
        }

        return currentGroup;
    }

    public String getPlayerHighestRankingGroup(final User user)
    {
        final GroupsConfig config = getGroupConfig(user);

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(user))
        {
            if (config.groups.containsKey(group))
            {
                final int rank = config.groups.get(group).rank;

                if (config.groups.containsKey(group) && rank > currentRank)
                {
                    currentGroup = group;
                    currentRank = rank;
                }
            }
        }

        return currentGroup;
    }

    public Integer timeToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = getGroupConfig(user);
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);

        final AccountConfigData.PlayerConfig playerConfig = playerData.playerData.get(user.getUniqueId());

        final String nextGroup = getNextGroup(user);

        if (nextGroup.equals(""))
        {
            return -0;
        }

        return groupsConfig.groups.get(nextGroup).timingTime - playerConfig.timePlayed;
    }

    public Integer balanceToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

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

        if (getNextGroup(user).equals(""))
        {
            return 0;
        }
        return groupsConfig.groups.get(getNextGroup(user)).moneyNeeded - userMoney;
    }

    public String getNextGroup(final Player player)
    {
        final String currentGroup = getPlayerHighestRankingGroup(player);
        final GroupsConfig config = getGroupConfig(player);
        final Map<Integer, String> ranksAndGroups = new HashMap<>();
        final int nextRank = config.groups.get(currentGroup).rank + 1;
        config.groups.forEach((name, conf) ->
                                ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }
        return "";
    }

    public String getNextGroup(final User player)
    {
        final String currentGroup = getPlayerHighestRankingGroup(player);

        if (currentGroup.equals(""))
        {
            return "";
        }

        final GroupsConfig config = getGroupConfig(player);

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = config.groups.get(currentGroup).rank + 1;

        config.groups.forEach((name, conf) ->
                                ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }

        return "";
    }

    public String getPreviousGroup(final Player player, final int currentRank)
    {
        final GroupsConfig config = getGroupConfig(player);

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = currentRank - 1;

        config.groups.forEach((name, conf) ->
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

        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        for (final Subject subject : getPlayerGroups(player))
        {
            ids.add(subject.getIdentifier());
        }

        if (ids.isEmpty())
        {
            ids.add(coreConfig.defaultGroup);
        }

        return ids;
    }

    public List<String> getPlayerGroupIds(User user)
    {
        final List<String> ids = new ArrayList<>();

        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        for (final Subject subject : getPlayerGroups(user))
        {
            ids.add(subject.getIdentifier());
        }

        if (ids.isEmpty())
        {
            ids.add(coreConfig.defaultGroup);
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
