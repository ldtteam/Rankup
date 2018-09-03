package com.ldt.rankup.modules.magibridge;

import com.ldt.rankup.modules.magibridge.config.MagibridgeConfigAdapter;
import com.ldt.rankup.qsml.modulespec.ConfigurableModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

/**
 * Magibridge integration module
 */
@ModuleData(id = MagibridgeModule.ID, name = "magibridge")
public class MagibridgeModule extends ConfigurableModule<MagibridgeConfigAdapter>
{

    public static final String ID = "magibridge";

    @Override
    protected MagibridgeConfigAdapter createConfigAdapter()
    {
        return new MagibridgeConfigAdapter();
    }
}
