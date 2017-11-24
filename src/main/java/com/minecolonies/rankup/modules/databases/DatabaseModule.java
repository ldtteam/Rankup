package com.minecolonies.rankup.modules.databases;

import com.minecolonies.rankup.modules.databases.config.DatabaseConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import org.spongepowered.api.service.sql.SqlService;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = DatabaseModule.ID, name = "Databases")
public class DatabaseModule extends ConfigurableModule<DatabaseConfigAdapter>
{
    public static final String ID = "databases";
    private SqlService sql;

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
