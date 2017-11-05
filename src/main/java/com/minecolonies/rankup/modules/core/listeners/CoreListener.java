package com.minecolonies.rankup.modules.core.listeners;

import com.minecolonies.rankup.internal.listener.ListenerBase;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.UUID;

/**
 * Listeners used by the Core Module.
 */
public class CoreListener extends ListenerBase
{
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player)
    {
        plugin.getLogger().info("Player Group: " + plugin.perms.getPlayerHighestRankingGroup(player));
        updatePlayerInfo(player);
    }

    private synchronized void updatePlayerInfo(final Player player)
    {
        CoreConfig config = plugin.configUtils.getCoreConfig();
        final UUID uuid = player.getUniqueId();

        if (plugin.accUtils.doesPlayerExist(uuid))
        {
            final String message = config.welcomeMessage
                                     .replace("{player}", player.getName())
                                     .replace("{prefix}", player.getOption("prefix").orElse(config.prefixFallback));

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));

            plugin.accUtils.updatePlayer(uuid);
        }
        else
        {
            final String message = config.firstWelcomeMessage
                                     .replace("{player}", player.getName())
                                     .replace("{prefix}", player.getOption("prefix").orElse(config.prefixFallback));

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));

            plugin.accUtils.addPlayer(uuid);
        }
    }
}
