package com.minecolonies.rankup.modules.economy.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

public class EconomyConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<EconomyConfig>
{

    public EconomyConfigAdapter()
    {
        // Required thanks to type erasure.
        super(EconomyConfig.class);
    }
}
