package io.github.asherslab.luckrankup.utils;

import com.google.common.reflect.TypeToken;
import io.github.asherslab.luckrankup.Luckrankup;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ConfigUtils
{
    private CommentedConfigurationNode config;
    private Path                       potentialPath;
    private File                       potentialFile;
    private Luckrankup                 plugin;

    private ConfigurationLoader<CommentedConfigurationNode> statsManager;
    private CommentedConfigurationNode                      stats;

    public CommentedConfigurationNode stats()
    {
        return stats;
    }

    public ConfigUtils(Luckrankup plugin, Path path, File file)
    {
        this.potentialPath = path;
        this.potentialFile = file;
        this.plugin = plugin;

        try
        {
            Files.createDirectories(potentialPath);
            if (!potentialFile.exists())
            {
                plugin.getLogger().info("Creating config file...");
                potentialFile.createNewFile();
            }

            config = plugin.getCfManager().load();

            config.getNode("date-format").setValue(config.getNode("date-format").getString("dd/MM/yyyy"))
              .setComment("Date format to save data info of players.");

            config.getNode("luck-track").setValue(config.getNode("luck-track").getString("member"))
              .setComment("The track we are using for LuckPerms");

            config.getNode("update-player-time-minutes").setValue(config.getNode("update-player-time-minutes").getInt(1))
              .setComment("The amount of minutes between player time updates");

            try
            {
                config.getNode("exclude-groups")
                  .setValue(config.getNode("exclude-groups").getList(TypeToken.of(String.class), Arrays.asList("admin", "mod", "dev", "helper", "patreon")))
                  .setComment("List of groups which will not be considered when checking for possible rank up scenarios");
            }
            catch (ObjectMappingException e)
            {
                e.printStackTrace();
            }

            config.getNode("ranked-groups").setComment("All configurations for promote players based on requirements.");
            if (!config.getNode("ranked-groups").hasMapChildren())
            {
                try
                {
                    config.getNode("ranked-groups", "groups")
                      .setComment("The groups that will be iterated through in the Luckperms Track, DO NOT LIST FINAL GROUP")
                      .setValue(config.getNode("groups")
                                  .getList(TypeToken.of(String.class),
                                    Arrays.asList("default", "newbie", "rookie", "trainee", "member", "citizen", "colonist", "mayor", "governor", "veteran")));
                }
                catch (ObjectMappingException e)
                {
                    e.printStackTrace();
                }

                for (String group : getStringList("ranked-groups", "groups"))
                {

                    config.getNode("ranked-groups", group, "time")
                      .setValue(config.getNode("time").getInt(5))
                      .setComment("This is the time it takes to go up to the next group");
                    try
                    {
                        config.getNode("ranked-groups", group, "commands")
                          .setValue(config.getNode("commands").getList(TypeToken.of(String.class), Arrays.asList("give {player} minecraft:diamond 1")))
                          .setComment("This is the command to run on {player} RankUp ");
                    }
                    catch (ObjectMappingException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (IOException e1)
        {
            plugin.getLogger().error("The default configuration could not be loaded or created!");
            e1.printStackTrace();
            return;
        }

        loadPlayerStats();

        save();
        plugin.getLogger().info("All configurations loaded!");
    }

    public void setConfig(Object value, Object... key)
    {
        config.getNode(key).setValue(value);
    }

    public Boolean getBool(Object... key)
    {
        return config.getNode(key).getBoolean();
    }

    public String getString(Object... key)
    {
        return config.getNode(key).getString();
    }

    public Integer getInt(Object... key)
    {
        return config.getNode(key).getInt();
    }

    public List<Integer> getIntList(Object... key)
    {
        try
        {
            return config.getNode(key).getList(TypeToken.of(Integer.class));
        }
        catch (ObjectMappingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStringList(Object... key)
    {
        try
        {
            return config.getNode(key).getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void save()
    {
        try
        {
            plugin.getCfManager().save(config);
        }
        catch (IOException e)
        {
            plugin.getLogger().error("Problems during save file:");
            e.printStackTrace();
        }
    }

    public void savePlayerStats()
    {
        try
        {
            statsManager.save(stats);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void loadPlayerStats()
    {
        File pStats = new File(potentialPath + File.separator + "playerstats.conf");
        try
        {
            if (!pStats.exists())
            {
                pStats.createNewFile();
            }

            statsManager = HoconConfigurationLoader.builder().setFile(pStats).build();
            stats = statsManager.load();
        }
        catch (IOException exception)
        {
            plugin.getLogger().error("The default configuration could not be loaded or created!");
            exception.printStackTrace();
        }
    }

    public void addPlayer(Player player)
    {
        String PlayerString = player.getUniqueId().toString();
        stats.getNode(PlayerString, "PlayerName").setValue(player.getName());
        stats.getNode(PlayerString, "JoinDate").setValue(CommonUtils.DateNow());
        stats.getNode(PlayerString, "LastVisit").setValue(CommonUtils.DateNow());
        stats.getNode(PlayerString, "TimePlayed").setValue(0);
        savePlayerStats();
    }

    public int getStatInt(String... key)
    {
        return stats.getNode(key).getInt();
    }

    public String getStatString(String... key)
    {
        return stats.getNode(key).getString();
    }

    public List<String> getStatsStringList(Object... key)
    {
        try
        {
            return stats.getNode(key).getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public int getPlayerTime(String uuid)
    {
        return stats.getNode(uuid, "TimePlayed").getInt();
    }

    public String getPlayerKey(User player)
    {
        if (stats.getNode(player.getUniqueId().toString(), "PlayerName").getString() != null)
        {
            return player.getUniqueId().toString();
        }
        return null;
    }

    public int setPlayerTime(String player, int amount)
    {
        stats.getNode(player, "TimePlayed").setValue(amount);
        return amount;
    }

    public int addPlayerTime(User player, int ammount)
    {
        String PlayerString = player.getUniqueId().toString();
        int time = stats.getNode(PlayerString, "TimePlayed").getInt();
        stats.getNode(PlayerString, "TimePlayed").setValue(time + ammount);
        return time + ammount;
    }

    public int addPlayerTime(String player, int amount)
    {
        int time = stats.getNode(player, "TimePlayed").getInt();
        stats.getNode(player, "TimePlayed").setValue(time + amount);
        return time + amount;
    }

    public void addPlayerTimes()
    {
        for (Player player : Sponge.getServer().getOnlinePlayers())
        {
            addPlayerTime(player, getInt("update-player-time-minutes"));
            checkRankup(player);
        }
        savePlayerStats();
    }

    public int checkRankup(Player player)
    {
        final String group = plugin.perms.getPlayerGroupWithMostParents(player);
        final List<String> groups = plugin.perms.getPlayerGroupIdentifiers(player);

        if (player == null)
        {
            return -1;
        }

        for (String g : groups)
        {
            for (String excl : plugin.cfgs.getStringList("excluded-groups"))
            {
                if (excl.equalsIgnoreCase(g))
                {
                    plugin.getLogger().info("Invalid Player");
                    return -1;
                }
            }
        }

        if (plugin.ourGroupsAndTimes.get(group) == null)
        {
            return -1;
        }

        if (getPlayerTime(player.getUniqueId().toString()) >= plugin.ourGroupsAndTimes.get(group))
        {
            String cmd = "lp user {player} promote {track}";

            String track = getString("luck-track");

            plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("{player}", player.getName()).replace("{track}", track));

            for (String command : getStringList("ranked-groups", group, "commands"))
            {
                plugin.game.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("{player}", player.getName()));
            }
            return 0;
        }

        return plugin.ourGroupsAndTimes.get(group);
    }
}
