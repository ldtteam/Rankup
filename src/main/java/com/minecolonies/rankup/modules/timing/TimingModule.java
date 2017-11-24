package com.minecolonies.rankup.modules.timing;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.timing.config.TimingConfig;
import com.minecolonies.rankup.modules.timing.config.TimingConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import com.minecolonies.rankup.util.RankingUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.util.concurrent.TimeUnit;

/**
 * Timing Module for the Rankup2 plugin.
 */
@ModuleData(id = TimingModule.ID, name = "Timing")
public class TimingModule extends ConfigurableModule<TimingConfigAdapter>
{

    public static final String ID = "timing";
    @Inject
    protected Rankup plugin;

    @Override
    protected TimingConfigAdapter createConfigAdapter()
    {
        return new TimingConfigAdapter();
    }

    @Override
    public void Rankup2Enable()
    {
        super.Rankup2Enable();
        playerCounterHandler();
    }

    public void playerCounterHandler()
    {
        TimingConfig timeConfig = plugin.getConfigUtils().getTimingConfig();

        getPlugin().getLogger().info("Updating player times every " + timeConfig.updateInterval + " minute(s)!");

        Sponge.getScheduler().createSyncExecutor(getPlugin()).scheduleWithFixedDelay(this::playerTimeAdd, timeConfig.updateInterval, timeConfig.updateInterval, TimeUnit.MINUTES);
    }

    public synchronized void playerTimeAdd()
    {
        TimingConfig timeConfig = plugin.getConfigUtils().getTimingConfig();

        for (final Player player : Sponge.getServer().getOnlinePlayers())
        {
            plugin.getAccUtils().addPlayerTime(player.getUniqueId(), timeConfig.updateInterval);
            RankingUtils.timeUp(player, plugin);
            RankingUtils.timeDown(player, plugin);
        }

        CoreConfig coreConfig = getPlugin().getConfigUtils().getCoreConfig();

        if (coreConfig.debugMode)
        {
            plugin.getLogger().info("Times updated for " + Sponge.getServer().getOnlinePlayers().size() + " player(s)");
        }
    }
}
