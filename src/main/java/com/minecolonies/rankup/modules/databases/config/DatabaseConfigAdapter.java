package com.minecolonies.rankup.modules.databases.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

/**
 * Connects the main config file with the {@link DatabaseConfig} file.
 */
public class DatabaseConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<DatabaseConfig>
{
    public DatabaseConfigAdapter()
    {
        // Required thanks to type erasure.
        super(DatabaseConfig.class);
    }
}
