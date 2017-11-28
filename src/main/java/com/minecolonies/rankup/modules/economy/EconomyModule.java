package com.minecolonies.rankup.modules.economy;

import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.economy.config.EconomyConfig;
import com.minecolonies.rankup.modules.economy.config.EconomyConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import com.minecolonies.rankup.util.RankingUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.enums.LoadingStatus;

import java.util.concurrent.TimeUnit;

/**
 * Economy Module for the Rankup2 plugin.
 */
@ModuleData(id = EconomyModule.ID, name = "Economy", status = LoadingStatus.DISABLED)
public class EconomyModule extends ConfigurableModule<EconomyConfigAdapter>
{

    public static final String ID = "economy";

    @Override
    protected EconomyConfigAdapter createConfigAdapter()
    {
        return new EconomyConfigAdapter();
    }

    @Override
    public void Rankup2Enable()
    {
        super.Rankup2Enable();
        economyHandler();
    }

    public void economyHandler()
    {
        EconomyConfig config = getPlugin().getConfigUtils().getEconomyConfig();

        getPlugin().getLogger().info("checking player balances every {} minute(s)!", config.updateInterval);

        Sponge.getScheduler().createSyncExecutor(getPlugin()).scheduleWithFixedDelay(this::playerCheckBalances, config.updateInterval, config.updateInterval, TimeUnit.MINUTES);
    }

    public void playerCheckBalances()
    {
        for (final Player player : Sponge.getServer().getOnlinePlayers())
        {
            RankingUtils.balanceCheck(player, getPlugin());
        }

        CoreConfig coreConfig = getPlugin().getConfigUtils().getCoreConfig();

        if (coreConfig.debugMode)
        {
            getPlugin().getLogger().info("Balances checked for {} player(s)", Sponge.getServer().getOnlinePlayers().size());
        }
    }
}
