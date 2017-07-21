package io.github.asherslab.luckrankup;

import com.google.inject.Inject;
import io.github.asherslab.luckrankup.utils.ConfigUtils;
import io.github.asherslab.luckrankup.utils.PermissionsUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(
  id = "luckrankup",
  name = "Luckrankup",
  description = "An addition for the Luckperms plugin that enables Auto-Ranking",
  authors = {"Asherslab"},
  dependencies = @Dependency(id = "luckperms", optional = false))
public class Luckrankup
{
    public Game             game;
    public PermissionsUtils perms;
    public ConfigUtils      cfgs;
    public HashMap<String, Integer> ourGroupsAndTimes = new HashMap<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    public ConfigurationLoader<CommentedConfigurationNode> getCfManager()
    {
        return configManager;
    }

    private PluginContainer instance;

    public PluginContainer get()
    {
        return this.instance;
    }

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event)
    {
        game = Sponge.getGame();

        instance = Sponge.getPluginManager().getPlugin("luckrankup").orElse(null);

        logger.info("Init config module...");
        configManager = HoconConfigurationLoader.builder().setFile(defConfig).build();
        cfgs = new ConfigUtils(this, configDir, defConfig);

        List<String> groups = cfgs.getStringList("ranked-groups", "groups");

        logger.info("Init commands module...");
        Commands.init(this);

        logger.info("Init permissions module...");
        perms = new PermissionsUtils(this, game);

        perms.getGroups().getAllSubjects().forEach(i -> this.logger.info("Our groups: " + i.getIdentifier() + " NUMBER: " + i.toString()));

        Iterator<String> groups_iterator = groups.iterator();

        while (groups_iterator.hasNext())
        {
            String group = groups_iterator.next();
            ourGroupsAndTimes.put(group, cfgs.getInt("ranked-groups", group, "time"));
        }

        this.logger.info("Group list: " + ourGroupsAndTimes);

        playerCounterHandler();
    }

    public Logger getLogger()
    {
        return logger;
    }

    private void playerCounterHandler()
    {
        this.logger.info("Updating player times every " + cfgs.getInt("update-player-time-minutes") + " minute(s)!");

        Sponge.getScheduler().createSyncExecutor(this).scheduleWithFixedDelay(new Runnable()
        {
            public void run()
            {
                cfgs.addPlayerTimes();
            }
        }, cfgs.getInt("update-player-time-minutes"), cfgs.getInt("update-player-time-minutes"), TimeUnit.MINUTES);
    }

    public void reload()
    {
        for (Task task : Sponge.getScheduler().getScheduledTasks(this))
        {
            task.cancel();
        }
        cfgs.savePlayerStats();
        cfgs = new ConfigUtils(this, configDir, defConfig);
        playerCounterHandler();
    }

    public void loadall()
    {
        for (Task task : Sponge.getScheduler().getScheduledTasks(this))
        {
            task.cancel();
        }
        cfgs = new ConfigUtils(this, configDir, defConfig);
        playerCounterHandler();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player)
    {
        if (cfgs.getPlayerKey(player) == null)
        {
            cfgs.addPlayer(player);
        }

        player.sendMessage(Text.of("Your current group: " + perms.getPlayerGroupWithMostParents(player)));
    }

    @Listener
    public void onStopServer(GameStoppedServerEvent e)
    {
        cfgs.savePlayerStats();
        for (Task task : Sponge.getScheduler().getScheduledTasks(this))
        {
            task.cancel();
        }
        logger.error("RankUpper disabled.");
    }
}
