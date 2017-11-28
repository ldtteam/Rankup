package com.minecolonies.rankup.modules.purchase.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

/**
 * Connects the main config file with the {@link PurchaseConfig} file.
 */
public class PurchaseConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<PurchaseConfig>
{

    public PurchaseConfigAdapter()
    {
        // Required thanks to type erasure.
        super(PurchaseConfig.class);
    }
}
