package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

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
        GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        List<Subject> disabledGroups = new ArrayList<>();

        for (final Subject subject : getGroups().getLoadedSubjects())
        {
            final String id = subject.getIdentifier();

            if (config.groups.containsKey(id) && !config.groups.get(id).enabled)
            {
                disabledGroups.add(subject);
            }
        }

        return disabledGroups;
    }

    public String getPlayerHighestRankingGroup(Player player)
    {
        final GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(player))
        {

            final int rank = config.groups.get(group).rank;

            if (config.groups.containsKey(group) && rank > currentRank)
            {
                currentGroup = group;
                currentRank = rank;
            }
        }

        return currentGroup;
    }

    public String getPlayerHighestRankingGroup(User user)
    {
        final GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        String currentGroup = "";
        Integer currentRank = -1;

        for (final String group : getPlayerGroupIds(user))
        {

            final int rank = config.groups.get(group).rank;

            if (config.groups.containsKey(group) && rank > currentRank)
            {
                currentGroup = group;
                currentRank = rank;
            }
        }

        return currentGroup;
    }

    public Integer timeToNextGroup(final User user)
    {
        final GroupsConfig groupsConfig = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);

        final AccountConfigData.PlayerConfig playerConfig = playerData.playerData.get(user.getUniqueId());

        final String currentGroup = getPlayerHighestRankingGroup(user);
        final String nextGroup = getNextGroup(groupsConfig.groups.get(currentGroup).rank);

        if (nextGroup.equals(""))
        {
            return -1;
        }

        return groupsConfig.groups.get(nextGroup).timingTime - playerConfig.timePlayed;
    }

    public String getNextGroup(final String currentGroup)
    {
        final GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

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

    public String getNextGroup(final int currentRank)
    {
        final GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

        final Map<Integer, String> ranksAndGroups = new HashMap<>();

        final int nextRank = currentRank + 1;

        config.groups.forEach((name, conf) ->
                                ranksAndGroups.put(conf.rank, name));

        if (ranksAndGroups.containsKey(nextRank))
        {
            return ranksAndGroups.get(nextRank);
        }

        return "";
    }

    public String getPreviousGroup(final int currentRank)
    {
        final GroupsConfig config = (GroupsConfig) plugin.getAllConfigs().get(GroupsConfig.class);

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

        for (final SubjectReference subject : getPlayerGroups(player))
        {
            ids.add(subject.getSubjectIdentifier());
        }

        return ids;
    }

    public List<String> getPlayerGroupIds(User user)
    {
        final List<String> ids = new ArrayList<>();

        for (final SubjectReference subject : getPlayerGroups(user))
        {
            ids.add(subject.getSubjectIdentifier());
        }

        return ids;
    }
}
