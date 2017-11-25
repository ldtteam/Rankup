package com.minecolonies.rankup.modules.databases;

import com.minecolonies.rankup.modules.databases.config.DatabaseConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = DatabaseModule.ID, name = "Databases")
public class DatabaseModule extends ConfigurableModule<DatabaseConfigAdapter>
{
    public static final String ID = "databases";

    public DatabaseModule()
    {
        //Public constructor to override the implicit one.
    }

    @Override
    protected DatabaseConfigAdapter createConfigAdapter()
    {
        return new DatabaseConfigAdapter();
    }

    @Override
    public void Rankup2Enable()
    {
        getPlugin().getLogger().info("Started Database module");
        getPlugin().getAccUtils().createTableIfNeeded();
    }
}
