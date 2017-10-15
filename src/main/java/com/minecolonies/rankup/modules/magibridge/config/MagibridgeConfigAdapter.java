package com.minecolonies.rankup.modules.magibridge.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

/**
 * Connects the main config file with the {@link MagibridgeConfig} file.
 */
public class MagibridgeConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<MagibridgeConfig>
{

    public MagibridgeConfigAdapter()
    {
        // Required thanks to type erasure.
        super(MagibridgeConfig.class);
    }
}
