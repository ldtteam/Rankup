package com.ldt.rankup.modules.magibridge.listeners;

import com.ldt.rankup.internal.events.RURankupEvent;
import com.ldt.rankup.internal.listener.ListenerBase;
import com.ldt.rankup.modules.magibridge.config.MagibridgeConfig;
import com.ldt.rankup.util.Constants;
import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.api.MagiBridgeAPI;
import net.dv8tion.jda.core.entities.TextChannel;
import org.spongepowered.api.event.Listener;

/**
 * Listeners used by the Magibridge Module.
 */
public class MagibridgeListener extends ListenerBase
{

    @Listener
    public void onRankup(RURankupEvent event)
    {

        final MagibridgeConfig magibridgeConfig = plugin.getConfigUtils().getMagibridgeConfig();

        String channelID;

        if (magibridgeConfig.sendInStaff)
        {
            channelID = MagiBridge.getConfig().CHANNELS.NUCLEUS.STAFF_CHANNEL;
        }
        else
        {
            channelID = MagiBridge.getConfig().CHANNELS.MAIN_CHANNEL;
        }

        final MagiBridgeAPI api = new MagiBridgeAPI();

        final TextChannel channel = api.getJDA().getTextChannelById(channelID);

        final String msg = magibridgeConfig.rankupMessage
                             .replace(Constants.PlayerInfo.PLAYER_NAME, event.getPlayer().getName())
                             .replace(Constants.ModuleInfo.MAGIBRIDGE_NEXT, event.getNextGroup())
                             .replace(Constants.ModuleInfo.MAGIBRIDGE_CURR, event.getCurrentGroup());

        api.sendMessageToChannel(channel, msg);
    }
}
