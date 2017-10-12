package com.minecolonies.luckrankup2.modules.timing.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

/**
 * Created by Asher on 8/10/17.
 */
public class TimingConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<TimingConfig>
{

    public TimingConfigAdapter()
    {
        // Required thanks to type erasure.
        super(TimingConfig.class);
    }
}
