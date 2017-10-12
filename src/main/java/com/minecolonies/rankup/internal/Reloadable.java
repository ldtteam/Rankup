package com.minecolonies.luckrankup2.internal;

/**
 * Marks a command or listener as reloadable - the {@link #onReload()} method will be called whenever the plugin config is reloaded.
 */
public interface Reloadable
{

    /**
     * Fired when the plugin configuration is reloaded.
     */
    void onReload();
}