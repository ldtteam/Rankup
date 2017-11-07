package com.minecolonies.rankup.modules.core.listeners;

import com.minecolonies.rankup.internal.listener.ListenerBase;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Listeners used by the Core Module.
 */
public class CoreListener extends ListenerBase
{
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player)
    {
        plugin.getLogger().info("Player Group: " + CoreModule.perms.getPlayerHighestRankingGroup(player));
        updatePlayerInfo(player);
    }

    private synchronized void updatePlayerInfo(final Player player)
    {
        AccountConfigData data = (AccountConfigData) this.plugin.getAllConfigs().get(AccountConfigData.class);
        CoreConfig config = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        if (data.playerData.containsKey(player.getUniqueId()))
        {
            final String message = config.welcomeMessage
                                     .replace("{player}", player.getName())
                                     .replace("{prefix}", player.getOption("prefix").orElse(config.prefixFallback));

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));

            if (!data.playerData.get(player.getUniqueId()).playerName.equals(player.getName()))
            {
                data.playerData.get(player.getUniqueId()).playerName = player.getName();
            }

            if (data.playerData.get(player.getUniqueId()).joinDate.isEmpty())
            {
                data.playerData.get(player.getUniqueId()).joinDate = dateNow();
            }

            data.playerData.get(player.getUniqueId()).lastVisit = dateNow();
        }
        else
        {
            final String message = config.firstWelcomeMessage
                                     .replace("{player}", player.getName())
                                     .replace("{prefix}", player.getOption("prefix").orElse(config.prefixFallback));

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));

            data.playerData.put(player.getUniqueId(), new AccountConfigData.PlayerConfig());

            data.playerData.get(player.getUniqueId()).joinDate = dateNow();
            data.playerData.get(player.getUniqueId()).lastVisit = dateNow();
            data.playerData.get(player.getUniqueId()).playerName = player.getName();
            data.playerData.get(player.getUniqueId()).timePlayed = 0;
        }

        data.save();
    }

    private String dateNow()
    {
        CoreConfig config = plugin.getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        DateFormat dateFormat = new SimpleDateFormat(config.dateFormat);
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}
