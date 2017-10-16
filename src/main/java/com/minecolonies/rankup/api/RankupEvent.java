package com.minecolonies.rankup.api;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

/**
 * Created by Asher on 14/10/17.
 */
public interface RankupEvent extends Cancellable, Event
{

    /**
     * Gets the {@link Player} that this event was fired
     */
    Player getPlayer();

    /**
     * Gets the Group that the player has been ranked up into
     *
     * @return the Group
     */
    String getNextGroup();

    /**
     * Gets the Group that the player has been ranked out of
     *
     * @return the Group
     */
    String getCurrentGroup();
}
