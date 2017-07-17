package io.github.asherslab.luckrankup.utils;

import io.github.asherslab.luckrankup.Luckrankup;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PermissionsUtils
{
    private PermissionService permissionService;
    private Luckrankup        plugin;

    public PermissionsUtils(Luckrankup pl, Game game)
    {
        this.plugin = pl;
        this.permissionService = game.getServiceManager().getRegistration(PermissionService.class).get().getProvider();
    }

    /*
    public String getPlayersHighestPerm(Player player)
    {
        List<String> groups = getPlayerGroupIdentifiers(player);
        List<String> permgroups = plugin.cfgs.getStringList("ranked-groups","groups");
        getPlayerGroups(player).forEach(Subject::getParents);
    }
    */

    public SubjectCollection getGroups()
    {
        return permissionService.getGroupSubjects();
    }

    public List<Subject> getPlayerGroups(Player player)
    {
        return player.getParents();
    }

    public List<String> getPlayerGroupIdentifiers(Player player)
    {
        List<String> identifiers = new ArrayList<String>();
        List<Subject> groups = getPlayerGroups(player);
        groups.forEach(group -> identifiers.add(group.getIdentifier()));
        return identifiers;
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
