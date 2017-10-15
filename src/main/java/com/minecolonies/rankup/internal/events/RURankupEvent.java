package com.minecolonies.rankup.internal.events;

import com.minecolonies.rankup.api.RankupEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * This is an event that is triggered whenever a player has been ranked up.
 */
public class RURankupEvent extends AbstractEvent implements RankupEvent
{
    private Player player;
    private String currentGroup;
    private String nextGroup;
    private Cause cause;
    private boolean isCancelled;

    public RURankupEvent(final Player player, final String currentGroup, final String nextGroup, final Cause cause)
    {
        this.player = player;
        this.currentGroup = currentGroup;
        this.nextGroup = nextGroup;
        this.cause = cause;
    }

    @Override
    public Cause getCause()
    {
        return this.cause;
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public String getCurrentGroup()
    {
        return this.currentGroup;
    }

    @Override
    public String getNextGroup()
    {
        return this.nextGroup;
    }

    @Override
    public boolean isCancelled()
    {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel)
    {
        this.isCancelled = cancel;
    }
}
