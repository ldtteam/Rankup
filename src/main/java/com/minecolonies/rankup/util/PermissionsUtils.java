package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<String> getDisabledGroups()
    {
        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        return coreConfig.disabledGroups;
    }

    public String getPlayerHighestRankingGroup(final Player player)
    {
        final GroupsConfig config = plugin.getConfigUtils().getGroupsConfig(player);

        if (config == null)
        {
            return "";
        }

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
        final GroupsConfig config = plugin.getConfigUtils().getGroupsConfig(user.getPlayer().orElse(null));

        if (config == null)
        {
            return "";
        }

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
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(user.getPlayer().orElse(null));

        final String nextGroup = getNextGroup(user);

        if ("".equals(nextGroup))
        {
            return -1;
        }

        return groupsConfig.groups.get(nextGroup).timingTime - plugin.getAccUtils().getPlayerTime(user.getUniqueId());
    }

    public Integer balanceToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(user.getPlayer().orElse(null));

        int userMoney;
        if (plugin.getEcon() != null && plugin.getEcon().getOrCreateAccount(user.getUniqueId()).isPresent())
        {
            UniqueAccount acc = plugin.getEcon().getOrCreateAccount(user.getUniqueId()).get();
            userMoney = acc.getBalance(plugin.getEcon().getDefaultCurrency()).intValue();
        }
        else
        {
            userMoney = 0;
        }

        if ("".equals(getNextGroup(user)) || groupsConfig == null)
        {
            return 0;
        }
        return groupsConfig.groups.get(getNextGroup(user)).moneyNeeded - userMoney;
    }

    public String getNextGroup(final Player player)
    {
        final String currentGroup = getPlayerHighestRankingGroup(player);

        final GroupsConfig config = plugin.getConfigUtils().getGroupsConfig(player);

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

        if ("".equals(currentGroup))
        {
            return "";
        }

        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player.getPlayer().orElse(null));

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

    public String getPreviousGroup(final Player player, final int currentRank)
    {
        final GroupsConfig groupsConfig = plugin.getConfigUtils().getGroupsConfig(player);

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

    public List<SubjectReference> getPlayerGroups(Player player)
    {
        return player.getParents();
    }

    public List<SubjectReference> getPlayerGroups(User user)
    {
        return user.getParents();
    }

    public List<String> getPlayerGroupIds(Player player)
    {
        final List<String> ids = new ArrayList<>();

        final CoreConfig coreConfig = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        for (final SubjectReference subject : getPlayerGroups(player))
        {
            ids.add(subject.getSubjectIdentifier());
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

        for (final SubjectReference subject : getPlayerGroups(user))
        {
            ids.add(subject.getSubjectIdentifier());
        }

        if (ids.isEmpty())
        {
            ids.add(coreConfig.defaultGroup);
        }

        return ids;
    }
}
