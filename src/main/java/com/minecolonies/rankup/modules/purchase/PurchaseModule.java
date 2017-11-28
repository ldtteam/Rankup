package com.minecolonies.rankup.modules.purchase;

import com.minecolonies.rankup.modules.purchase.config.PurchaseConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.enums.LoadingStatus;

/**
 * Purchase module class.
 */
@ModuleData(id = PurchaseModule.ID, name = "Purchase", status = LoadingStatus.DISABLED)
public class PurchaseModule extends ConfigurableModule<PurchaseConfigAdapter>
{

    public static final String ID = "purchase";

    @Override
    protected PurchaseConfigAdapter createConfigAdapter()
    {
        return new PurchaseConfigAdapter();
    }
}
