package com.minecolonies.rankup.modules.timing;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
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
        TimingConfig config = getPlugin().getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();

        getPlugin().getLogger().info("Updating player times every " + config.updateInterval + " minute(s)!");

        Sponge.getScheduler().createAsyncExecutor(getPlugin()).scheduleWithFixedDelay(this::playerTimeAdd, config.updateInterval, config.updateInterval, TimeUnit.MINUTES);
    }

    public synchronized void playerTimeAdd()
    {
        final TimingConfig timeConfig = plugin.getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();
        final AccountConfigData playerData = (AccountConfigData) plugin.getAllConfigs().get(AccountConfigData.class);

        for (final Player player : Sponge.getServer().getOnlinePlayers())
        {
            playerData.playerData.get(player.getUniqueId()).timePlayed += timeConfig.updateInterval;
            RankingUtils.timeUp(player, plugin);
            RankingUtils.timeDown(player, plugin);
        }

        final CoreConfig config = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        if (config.debugMode)
        {
            plugin.getLogger().info("Times updated for " + Sponge.getServer().getOnlinePlayers().size() + " player(s)");
        }
        playerData.save();
    }
}
