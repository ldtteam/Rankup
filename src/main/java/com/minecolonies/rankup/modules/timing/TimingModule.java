package com.minecolonies.luckrankup2.modules.timing;

import com.minecolonies.luckrankup2.modules.core.config.AccountConfigData;
import com.minecolonies.luckrankup2.modules.timing.config.TimingConfig;
import com.minecolonies.luckrankup2.modules.timing.config.TimingConfigAdapter;
import com.minecolonies.luckrankup2.qsml.modulespec.ConfigurableModule;
import com.minecolonies.luckrankup2.util.RankingUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.util.concurrent.TimeUnit;

/**
 * Timing Module for the Luckrankup2 plugin.
 */
@ModuleData(id = TimingModule.ID, name = "Timing")
public class TimingModule extends ConfigurableModule<TimingConfigAdapter>
{

    public static final String ID = "timing";

    @Override
    protected TimingConfigAdapter createConfigAdapter()
    {
        return new TimingConfigAdapter();
    }

    @Override
    public void luckrankup2Enable()
    {
        super.luckrankup2Enable();
        playerCounterHandler();
    }

    private void playerCounterHandler()
    {
        TimingConfig config = getPlugin().getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();

        getPlugin().getLogger().info("Updating player times every " + config.updateInterval + " minute(s)!");

        Sponge.getScheduler().createSyncExecutor(getPlugin()).scheduleWithFixedDelay(this::playerTimeAdd, config.updateInterval, config.updateInterval, TimeUnit.MINUTES);
    }

    private void playerTimeAdd()
    {
        TimingConfig timeConfig = getPlugin().getConfigAdapter(TimingModule.ID, TimingConfigAdapter.class).get().getNodeOrDefault();
        AccountConfigData playerData = (AccountConfigData) getPlugin().getAllConfigs().get(AccountConfigData.class);

        for (final Player player : Sponge.getServer().getOnlinePlayers())
        {
            playerData.playerData.get(player.getUniqueId()).timePlayed += timeConfig.updateInterval;
            RankingUtils.rankup(player, getPlugin());
            RankingUtils.rankdown(player, getPlugin());
        }

        getPlugin().getLogger().info("Times updated for " + Sponge.getServer().getOnlinePlayers().size() + " player(s)");
        playerData.save();
    }
}
