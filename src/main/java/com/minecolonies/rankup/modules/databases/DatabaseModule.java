package com.minecolonies.rankup.modules.databases;

import com.minecolonies.rankup.modules.databases.config.DatabaseConfigAdapter;
import com.minecolonies.rankup.qsml.modulespec.ConfigurableModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Asher on 4/11/17.
 */
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
    }

    public DataSource getDataSource(String jdbcUrl) throws SQLException
    {
        if (sql == null)
        {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    public void myMethodThatQueries() throws SQLException
    {
        String uri = "jdbc:h2:text.db";
        String sql = "SELECT * FROM test_tbl";

        try (Connection conn = getDataSource(uri).getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery())
        {

            while (results.next())
            {
                getPlugin().getLogger().info("SQL: " + results.getString(0));
            }
        }
    }
}
