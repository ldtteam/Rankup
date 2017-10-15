package com.minecolonies.rankup.internal;

/**
 * Marks a command or listeners as reloadable - the {@link #onReload()} method will be called whenever the plugin config is reloaded.
 */
public interface Reloadable
{

    /**
     * Fired when the plugin configuration is reloaded.
     */
    void onReload();
}