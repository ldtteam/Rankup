package com.minecolonies.rankup.modules.timing.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

public class TimingConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<TimingConfig>
{

    public TimingConfigAdapter()
    {
        // Required thanks to type erasure.
        super(TimingConfig.class);
    }
}
