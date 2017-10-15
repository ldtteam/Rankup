package com.minecolonies.rankup.modules.magibridge.listeners;

import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.api.MagiBridgeAPI;
import com.minecolonies.rankup.internal.events.RURankupEvent;
import com.minecolonies.rankup.internal.listener.ListenerBase;
import com.minecolonies.rankup.modules.magibridge.MagibridgeModule;
import com.minecolonies.rankup.modules.magibridge.config.MagibridgeConfig;
import com.minecolonies.rankup.modules.magibridge.config.MagibridgeConfigAdapter;
import net.dv8tion.jda.core.entities.TextChannel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;
import uk.co.drnaylor.quickstart.exceptions.UndisableableModuleException;

/**
 * Listeners used by the Magibridge Module.
 */
public class MagibridgeListener extends ListenerBase
{

    @Listener
    public void onRankup(RURankupEvent event)
    {
        if (Sponge.getPluginManager().isLoaded("magibridge"))
        {

            final MagibridgeConfig magibridgeConfig = plugin.getConfigAdapter(MagibridgeModule.ID, MagibridgeConfigAdapter.class).get().getNodeOrDefault();

            final String channelID = MagiBridge.getConfig().CHANNELS.NUCLEUS.GLOBAL_CHANNEL;

            final MagiBridgeAPI api = new MagiBridgeAPI();

            final TextChannel channel = api.getJDA().getTextChannelById(channelID);

            final String msg = magibridgeConfig.rankupMessage
                                 .replace("{player}", event.getPlayer().getName())
                                 .replace("{group}", event.getNextGroup());

            api.sendMessageToChannel(channel, msg);
        }
    }
}
